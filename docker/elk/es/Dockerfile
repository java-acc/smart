FROM elasticsearch:7.17.25
COPY elasticsearch.yml /usr/share/elasticsearch/config/elasticsearch.yml
RUN chown elasticsearch:elasticsearch /usr/share/elasticsearch/config/elasticsearch.yml
EXPOSE 9200 9300
CMD ["bin/elasticsearch"]