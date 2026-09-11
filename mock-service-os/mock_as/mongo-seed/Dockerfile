FROM mongo

COPY opin /mock_as/mongo-seed/opin

COPY start.sh /start.sh
RUN chmod +x /start.sh

CMD ["/start.sh", "my-replica-set/mongo3:30003,mongo2:30002,mongo1:30001"]
