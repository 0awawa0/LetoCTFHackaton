import re
from uuid import uuid4
import smtplib
import sqlite3
import requests
from time import time
from lxml import html
from flask import Flask
from pygost import gost34112012256
from binascii import hexlify
from flask_restful import Api, Resource, reqparse

salt = open("salt", "r").read()
password = open("password", "r").read()
host = "192.168.1.244"

app = Flask(__name__)
api = Api(app)


def get_news():
    r = requests.get('https://yandex.ru/news/rubric/koronavirus')
    h = html.fromstring(r.text)
    link = h.xpath('//*[@id="neo-page"]/div/div[2]/div/div[1]/div[2]/div[1]/article/div[2]/a')
    url = 'https://yandex.ru/' + link[0].attrib['href']
    urls = []
    headlings = []

    for i in range(2, 6):
        headling = h.xpath('//*[@id="neo-page"]/div/div[2]/div/div[1]/div[2]/div[{}]/article/a/h2'.format(i))
        link = h.xpath('//*[@id="neo-page"]/div/div[2]/div/div[1]/div[2]/div[{}]/article/a'.format(i))
        url = 'https://yandex.ru/' + link[0].attrib['href']
        urls.append(url)
        headlings.append(headling[0].text)

    return [headlings, urls]


def init_db():
    conn = sqlite3.connect('Enigma.db')

    conn.execute('''CREATE TABLE IF NOT EXISTS hospital (
        id_hospital          integer NOT NULL  PRIMARY KEY  ,
        city                 text     ,
        street               text     ,
        name_hospital        text NOT NULL    );''')

    conn.execute('''CREATE TABLE IF NOT EXISTS patient (
        id_patient           integer NOT NULL  PRIMARY KEY  ,
        name                 text NOT NULL    ,
        surname              text NOT NULL    ,
        email                text NOT NULL    ,
        passport             integer NOT NULL    ,
        password             text NOT NULL    ,
        phone                integer NOT NULL    ,
        date_birth           date NOT NULL    ,
        city                 text NOT NULL    ,
        street               text NOT NULL    ,
        num_oms              integer NOT NULL    ,
        token                text);''')

    conn.execute('''CREATE TABLE IF NOT EXISTS doctor (
        id_doctor            integer NOT NULL  PRIMARY KEY  ,
        fio                  text     ,
        num_cabinet          integer     ,
        id_hospital          integer NOT NULL    ,
        FOREIGN KEY ( id_hospital ) REFERENCES hospital( id_hospital ) ON DELETE CASCADE ON UPDATE CASCADE);''')

    conn.execute('''CREATE TABLE IF NOT EXISTS reception (
        id_reception         integer NOT NULL    ,
        id_patient           integer    ,
        id_doctor            integer NOT NULL    ,
        id_hospital          integer NOT NULL    ,
        reserved             integer NOT NULL DEFAULT 0    ,
        date_reception       timestamp NOT NULL    ,
        FOREIGN KEY ( id_patient ) REFERENCES patient( id_patient ) ON DELETE CASCADE ON UPDATE CASCADE,
        FOREIGN KEY ( id_doctor ) REFERENCES doctor( id_doctor )  ,
        FOREIGN KEY ( id_hospital ) REFERENCES hospital( id_hospital ));''')

    conn.execute('''CREATE TABLE IF NOT EXISTS unverified_users (
            id_patient           integer NOT NULL  PRIMARY KEY  ,
        name                 text NOT NULL    ,
        surname              text NOT NULL    ,
        email                text NOT NULL    ,
        passport             integer NOT NULL    ,
        password             text NOT NULL    ,
        phone                integer NOT NULL    ,
        date_birth           date NOT NULL    ,
        city                 text NOT NULL    ,
        street               text NOT NULL    ,
        num_oms              integer NOT NULL    ,
        email_token          text NOT NULL);''')

    conn.commit()
    conn.close()


def mailing():
    news = get_news()
    conn = sqlite3.connect('Enigma.db')
    emails = list(conn.execute("SELECT email FROM patient").fetchall())
    msg = '''
    Подборка новостей:
    {} - {}
    {} - {}
    {} - {}
    '''.format(news[0][0], news[1][0], news[0][1], news[1][1], news[0][2], news[1][2])
    conn.close()
    fromaddr = username = 'sinura.team@gmail.com'
    server = smtplib.SMTP('smtp.gmail.com:587')
    server.starttls()
    server.login(username, password)
    for email in emails:
        try:
            server.sendmail(fromaddr, email, msg.encode())
        except:
            pass
    server.quit()


class UserReg(Resource):
    def __init__(self):
        self.user_reg_keys = ["name", "surname", "email", "passport", "password", "phone", "date_birth", "city",
                              "street", "num_oms"]

    def get(self):
        parser = reqparse.RequestParser()
        parser.add_argument("token")
        params = parser.parse_args()
        if not params["token"] is None:
            conn = sqlite3.connect('Enigma.db')
            user = conn.execute(
                "SELECT * FROM unverified_users WHERE email_token = '{}'".format(params["token"])).fetchone()
            user = list(user)[:-1:]
            user = user[1::]
            conn.close()
            conn = sqlite3.connect('Enigma.db')
            conn.execute("INSERT INTO patient (name,surname,email,passport,password,phone,date_birth,city,street,"
                         "num_oms) VALUES ('{}','{}','{}',{},'{}',{},'{}','{}','{}',{})".format(user[0], user[1],
                                                                                                user[2], user[3],
                                                                                                user[4], user[5],
                                                                                                user[6], user[7],
                                                                                                user[8], user[9]))
            conn.commit()
            conn.close()
            return {"status": "User accepted"}, 200
        else:
            return {"status": "Invalid token"}, 400

    def post(self):
        parser = reqparse.RequestParser()

        for key in self.user_reg_keys:
            parser.add_argument(key)
        params = parser.parse_args()

        email_re = '^[a-z0-9A-ZА-Яа-я]+[\._]?[a-z0-9A-ZА-Яа-я]+[@]\w+[.]\w{2,3}$'
        if not re.search(email_re, params["email"]):
            return {"status": "Incorrect email"}, 400

        phone_re = '(\d{3}[-\.\s]??\d{3}[-\.\s]??\d{4}|\(\d{3}\)\s*\d{3}[-\.\s]??\d{4}|\d{3}[-\.\s]??\d{4})'
        if not re.search(phone_re, params["phone"]):
            return {"status": "Incorrect phone"}, 400

        for key in self.user_reg_keys:
            if not params[key]:
                return {"status": "Bad arguments"}, 400

        params["password"] = gost34112012256.new(salt.encode() + params["password"].encode()).hexdigest()
        conn = sqlite3.connect('Enigma.db')
        user = conn.execute("SELECT * from patient WHERE passport = {}".format(params["passport"])).fetchone()
        conn.close()
        if user:
            return {"status": "User exists"}, 400

        fromaddr = username = 'sinura.team@gmail.com'
        server = smtplib.SMTP('smtp.gmail.com:587')
        server.starttls()
        server.login(username, password)
        token = hexlify(str(uuid4()).encode()).decode()
        url = "{}/user/registration?token={}".format(host, token)
        msg = '''Здравствуйте! Ссылка для подтверждения регистрации - {}'''.format(url)
        server.sendmail(fromaddr, params["email"], msg.encode())
        server.quit()
        params["password"] = gost34112012256.new(salt.encode() + params["password"].encode()).hexdigest()
        conn = sqlite3.connect('Enigma.db')
        conn.execute("INSERT INTO unverified_users (name,surname,email,passport,password,phone,date_birth,city,street,"
                     "num_oms, email_token) VALUES ('{}','{}','{}',{},'{}',{},'{}','{}','{}',{}, '{}')".format(params["name"],
                                                                                            params["surname"],
                                                                                            params["email"],
                                                                                            params["passport"],
                                                                                            params["password"],
                                                                                            params["phone"],
                                                                                            params["date_birth"],
                                                                                            params["city"],
                                                                                            params["street"],
                                                                                            params["num_oms"],
                                                                                            str(token)))
        conn.commit()
        conn.close()
        return {"status": "Send register-url"}, 200


class UserLogin(Resource):
    def __init__(self):
        self.user_reg_keys = ["email", "password"]

    def post(self):
        parser = reqparse.RequestParser()

        for key in self.user_reg_keys:
            parser.add_argument(key)
        params = parser.parse_args()

        for key in self.user_reg_keys:
            if params[key] is None:
                return {"status": "Bad arguments"}, 400

        params["password"] = gost34112012256.new(salt.encode() + params["password"].encode()).hexdigest()
        conn = sqlite3.connect('Enigma.db')
        user = conn.execute(
            "SELECT id_patient FROM patient WHERE email = '{}' AND password = '{}'".format(params["email"], params[
                "password"])).fetchone()
        conn.close()

        if user is None:
            return {"status": "Incorrect email or password"}, 400

        id_patient = list(user)[0]
        token = uuid4()
        conn = sqlite3.connect('Enigma.db')
        conn.execute("UPDATE patient set token = '{}' WHERE id_patient = {}".format(token, id_patient))
        conn.commit()
        conn.close()
        return {"status": "User logged in", "token": str(token)}, 200


class User(Resource):
    def __init__(self):
        self.user_reg_keys = ["token", "name", "surname", "email", "passport", "password", "phone", "date_birth",
                              "city", "street", "num_oms"]

    def get(self):
        parser = reqparse.RequestParser()
        parser.add_argument("token")
        params = parser.parse_args()
        conn = sqlite3.connect('Enigma.db')
        user = conn.execute("SELECT id_patient, name, surname, email, passport, phone, date_birth, city, street, "
                            "num_oms FROM patient WHERE token = '{}'".format(params["token"]))
        user = list(user)[0]
        conn.close()
        if user:
            data = {}
            key = ["id_patient", "name", "surname", "email", "passport", "phone", "date_birth", "city", "street", "num_oms"]
            for inf in range(len(key)):
                data[key[inf]] = user[inf]
            return {"status": "User found", "user": data}, 200

        return {"status": "Token check failed"}, 400

    def put(self):
        parser = reqparse.RequestParser()

        for key in self.user_reg_keys:
            parser.add_argument(key)
        params = parser.parse_args()

        email_re = '^[a-z0-9]+[\._]?[a-z0-9]+[@]\w+[.]\w{2,3}$'
        if not re.search(email_re, params["email"]):
            return {"status": "Incorrect email"}

        phone_re = '([+]\d{3}[-\.\s]??\d{3}[-\.\s]??\d{4}|\(\d{3}\)\s*\d{3}[-\.\s]??\d{4}|\d{3}[-\.\s]??\d{4})'
        if not re.search(phone_re, params["phone"]):
            return {"status": "Incorrect phone"}

        params["password"] = gost34112012256.new(salt.encode() + params["password"].encode()).hexdigest()
        conn = sqlite3.connect('Enigma.db')
        user = conn.execute(
            "SELECT id_patient from patient WHERE password = '{}' AND token = '{}'".format(params["password"], params[
                "token"])).fetchone()
        conn.close()
        if user is None:
            return {"status": "Incorrect token or password"}, 400

        id_patient = list(user)[0]
        for key in self.user_reg_keys:
            conn = sqlite3.connect('Enigma.db')
            conn.execute("UPDATE patient set {} = '{}' where id_patient = {}".format(key, params["key"], id_patient))
            conn.commit()
            conn.close()

        return {"status": "User edited"}, 200


class News(Resource):
    def get(self):
        try:
            news = get_news()
            return {"status": "News parsed", "News": news}, 200
        except:
            return {"status": "Error while parsing"}, 400


class Reception(Resource):
    def __init__(self):
        self.hour = 60
        self.day = 86400
        self.week = 604800

    def get(self, current_time=time()):
        try:
            conn = sqlite3.connect('Enigma.db')
            id_doctor = conn.execute("SELECT id_doctor from "
                                     "reception WHERE {} < date_reception AND date_reception > {} ".format(
                current_time,
                current_time + self.week)).fetchall()
            conn.close()
            id_doctor = list(id_doctor)
            print(id_doctor)
            conn = sqlite3.connect('Enigma.db')
            recep_data = conn.execute("SELECT id_reception, id_hospital, date_reception, reserved from "
                                      "reception WHERE {} < date_reception AND date_reception > {} ".format(
                current_time,
                current_time + self.week)).fetchall()
            conn.close()
            recep_data = list(recep_data)

            for doc_id in range(len(id_doctor)):
                conn = sqlite3.connect('Enigma.db')
                doc_data = conn.execute(
                    "SELECT fio, num_cabinet FROM doctor WHERE id_doctor = {}".format(id_doctor[doc_id]))
                conn.close()
                doc_data = list(doc_data)
                for doc_inf in doc_data:
                    recep_data[doc_id].append(doc_inf)

            return {"status": "Receptions", "recep": recep_data}, 200
        except:
            return {"status": "Error while getting receptions"}, 400

    def post(self):
        parser = reqparse.RequestParser()
        parser.add_argument("token")
        parser.add_argument("reception")
        params = parser.parse_args()

        if params["token"] is None or params["reception"] is None:
            return {"status": "Bad arguments"}, 400

        conn = sqlite3.connect('Enigma.db')
        user = conn.execute(
            "SELECT id_patient from patient WHERE token = '{}'".format(params["token"])).fetchone()
        conn.close()
        user = list(user)

        conn = sqlite3.connect('Enigma.db')
        conn.execute(
            "UPDATE reception set id_patient = '{}' WHERE id_reception = {}".format(user[0], params["reception"]))
        conn.commit()
        conn.close()
        conn = sqlite3.connect('Enigma.db')
        conn.execute("UPDATE reception set reserved = {} WHERE id_reception = {}".format(1, params["reception"]))
        conn.commit()
        conn.close()
        return {"status": "Reception reserved"}, 200


api.add_resource(UserReg, "/user/registration")
api.add_resource(UserLogin, "/user/login")
api.add_resource(User, "/user")
api.add_resource(News, "/news")
api.add_resource(Reception, "/recep")

if __name__ == '__main__':
    init_db()
    mailing()
    app.run("0.0.0.0", port=80, debug=False)
