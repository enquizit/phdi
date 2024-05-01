FROM ghcr.io/cdcgov/phdi/dibbs

WORKDIR /code

COPY ./requirements.txt /code/requirements.txt
RUN pip install -r requirements.txt

COPY ./app /code/app
COPY ./description.md /code/description.md
COPY ./migrations /code/migrations
COPY ./assets /code/assets

ADD odbcinst.ini /etc/odbcinst.ini
RUN apt-get update
RUN apt-get install -y tdsodbc unixodbc-dev
RUN apt install unixodbc -y
RUN apt-get clean -y

EXPOSE 8080
CMD uvicorn app.main:app --host 0.0.0.0 --port 8080 --log-config app/log_config.yml 