# pull in EdgeDB CLI
FROM edgedb/edgedb AS edgedb
WORKDIR /myapp
ENV EDGEDB_CREDENTIALS_FILE=credentials.json
COPY edgedb.toml /myapp/edgedb.toml
COPY dbschema /myapp/dbschema
COPY credentials.json /myapp/credentials.json
RUN edgedb instance link db --credentials-file --overwrite --non-interactive
RUN edgedb migrate -I db


# Utiliser une image de base légère
FROM openjdk:17-jdk AS base
WORKDIR /myapp


# Installer Coursier
RUN curl -fL "https://github.com/coursier/launchers/raw/master/cs-x86_64-pc-linux.gz" | gzip -d > cs
RUN chmod +x cs
RUN mv cs /usr/local/bin/cs

# Installer Bleep
RUN cs install --channel https://raw.githubusercontent.com/oyvindberg/bleep/master/coursier-channel.json bleep
RUN mv /root/.local/share/coursier/bin/bleep /usr/local/bin/

# Copier l'app
COPY . .

# Créer executable
RUN bleep dist back


# Finally, build the production image with minimal footprint
FROM eclipse-temurin:17.0.14_7-jre-ubi9-minimal


# Change ownership of the .config directory
WORKDIR /myapp
COPY --from=base /myapp/.bleep/builds/normal/.bloop/back/dist /myapp/dist
COPY --from=edgedb /myapp/credentials.json /myapp/dist/credentials.json



ENTRYPOINT [""]
