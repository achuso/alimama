FROM postgres:15

ENV POSTGRES_USER=onat
ENV POSTGRES_PASSWORD=sifre1234
ENV POSTGRES_DB=alimama-postgres

COPY init.sql /docker-entrypoint-initdb.d/

# Port
EXPOSE 5432