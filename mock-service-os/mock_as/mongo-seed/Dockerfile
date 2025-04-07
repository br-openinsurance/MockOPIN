FROM mongo

COPY init_clients.json /init_clients.json
COPY init_accounts.json /init_accounts.json
COPY init_credentials.json /init_credentials.json

COPY start.sh /start.sh
RUN chmod +x /start.sh

CMD /start.sh "my-replica-set/mongo3:30003,mongo2:30002,mongo1:30001"