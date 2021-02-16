#!/usr/bin/env python3
# {{ ansible_managed }}

import psycopg2
from psycopg2.extensions import ISOLATION_LEVEL_AUTOCOMMIT
import os
import time


def config():
    return {'dbname': 'postgres',
            'user': os.getenv('PG_USER'),
            'password': os.getenv('PG_PASS'),
            'host': os.getenv('PG_HOST'),
            'port': os.getenv('PG_PORT')}


def wait_database(params, retry_count=100, retry_delay_in_sec=0.500):
    count = 1
    while count <= retry_count:
        count += 1
        try:
            print('Connecting to the PostgreSQL database... {0}/{1}'.format(count, retry_count))
            conn = psycopg2.connect(**params)
            cur = conn.cursor()
            cur.execute("SELECT version();")
            res = cur.fetchone()
            print(res)
            cur.close()
            return True
        except (Exception, psycopg2.DatabaseError) as error:
            # print(error)
            print('.')
        time.sleep(retry_delay_in_sec)
    return False


def execute(params, sqls):
    """ Connect to the PostgreSQL database server """
    conn = None
    try:
        # connect to the PostgreSQL server
        print('Connecting to the PostgreSQL database...')
        conn = psycopg2.connect(**params)
        conn.set_isolation_level(ISOLATION_LEVEL_AUTOCOMMIT)

        # create a cursor
        cur = conn.cursor()
        # execute a statement
        for sql in sqls:
            print(">", sql, "<")
            cur.execute(sql)
            if cur.rowcount > 0:
                res = cur.fetchone()
                print(res)

        # close the communication with the PostgreSQL
        cur.close()
    except (Exception, psycopg2.DatabaseError) as error:
        print(error)
    finally:
        if conn is not None:
            conn.close()
            print('Database connection closed.')


def file_to_sqls(filename, delimiter=';'):
    with open(filename, 'r') as file:
        sqls = file.read()
    sqls = sqls.split(delimiter)
    sqls = [x.strip() for x in sqls if len(x.strip()) > 0]  # remove empty string
    return sqls


if __name__ == '__main__':
    db_cfg = config()
    db_custom = {'dbname': 'busd'}
    if wait_database(db_cfg):
        execute(db_cfg, ['SELECT version()'])
        execute(db_cfg, file_to_sqls('create-utilities.sql', '-- ;;'))
        execute(db_cfg, file_to_sqls('create-db.sql'))
        execute(db_cfg, file_to_sqls('create-db-post-init.sql'))
        execute({**db_cfg, **db_custom}, file_to_sqls('create-utilities.sql', '-- ;;'))
        execute({**db_cfg, **db_custom}, file_to_sqls('create-schema.sql'))
    else:
        raise Exception('Unable to connect to database')
