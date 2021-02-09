FROM python:3.7-alpine
WORKDIR /scripts
RUN apk update \
 && apk add --no-cache \
        postgresql-dev \
        gcc \
        python3-dev \
        musl-dev \
        linux-headers \
        bash \
        curl
COPY requirements.txt requirements.txt
RUN pip install -r requirements.txt
COPY . .
RUN chmod +x ./init-db.py
CMD ["init-db.py"]
ENTRYPOINT ["python3"]
