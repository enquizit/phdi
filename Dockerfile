FROM python:3.12

RUN curl https://packages.microsoft.com/keys/microsoft.asc | tee /etc/apt/trusted.gpg.d/microsoft.asc && \
    curl https://packages.microsoft.com/config/ubuntu/22.04/prod.list | tee /etc/apt/sources.list.d/mssql-release.list && \
    ACCEPT_EULA=Y apt update && ACCEPT_EULA=Y apt install -y unixodbc mssql-tools18 && apt clean

WORKDIR /code

COPY sample/nbs /code/nbs
COPY src/ /code/src/

RUN pip install --no-cache-dir --upgrade -r /code/nbs/requirements.txt && pip install -e /code/src
CMD [ "fastapi", "run", "/code/nbs/main.py", "--port", "8000"]