class Config:
    DEBUG = True

    SECRET_KEY = "ad2xxN @NA13ka-1812 sak.i19 2i1k2"

    SQLALCHEMY_DATABASE_URI = "mysql+pymysql://bdisfer1410:caballoNEGRO231@bdisfer1410.mysql.eu.pythonanywhere-services.com/bdisfer1410$bd_controldepresencia"
    SQLALCHEMY_TRACK_MODIFICATIONS = False
    SQLALCHEMY_ENGINE_OPTIONS = {
            "pool_recycle": 280,
            "pool_pre_ping": True,
    }

    JWT_SECRET_KEY = "kajs912j1kmnckjad12usaksa910oi23d"
    JWT_ACCESS_TOKEN_EXPIRES = 900  # 15 minutos
    JWT_REFRESH_TOKEN_EXPIRES = 604800  # 7 días