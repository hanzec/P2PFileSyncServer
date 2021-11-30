FROM azul/zulu-openjdk-alpine:16-jre as builder

MAINTAINER Hanze Chen

# expose port app uses
EXPOSE 80/tcp

RUN addgroup sync_disk
RUN adduser -s /bin/false       \
            -h /opt/sync-disk   \
            --system            \
            --disabled-password \
            -G sync_disk        \
            sync_disk
COPY --chown=sync_disk:sync_disk build/libs/sync-disk.jar /opt/sync-disk/sync-disk.jar

USER sync_disk
WORKDIR /opt/sync-disk
ENV DATABASE_PASSWORD $DATABASE_PASSWORD
ENV DATABASE_USERNAME $DATABASE_USERNAME
ENTRYPOINT ["java", "-jar",                                      \
            "-Dspring.datasource.password=${DATABASE_PASSWORD}", \
            "-Dspring.datasource.username=${DATABASE_USERNAME}", \
            "/opt/sync-disk/sync-disk.jar"]
HEALTHCHECK --start-period=30s --interval=30s --timeout=3s --retries=3 \
            CMD curl -m 5 --silent --fail --request GET http://localhost/actuator/health | \
            jq --exit-status -n 'inputs | if has("status") then .status=="UP" else false end' > /dev/null || exit 1


