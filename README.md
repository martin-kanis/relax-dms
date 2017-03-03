# Relax DMS

This is my master thesis at BUT FIT. It's implementation of distributed document server based on CouchDB database.

Setting CouchDB cluster
-----------------------

Cluster is available through Docker image ([klaemo/couchdb:2.0-dev](http://localhost:8180/auth/admin). There are 3 configured CouchDB nodes wiht HAProxy in front them.
All you need is Docker installed on your machine.

1. Start Docker
    ````
    sudo systemctl start docker
    ```` 
2. Run Docker container
    ````
    sudo docker run --privileged=true -it -p 5984:5984 -v $(pwd)/couchdb:/usr/src/couchdb/dev/lib/node1/data klaemo/couchdb:2.0-dev --admin=admin:password --with-haproxy
    ````
    Note this will mount data from Docker CouchDB cluster to your host specifically to your current working directory.
3. Access cluster through proxy at port 5984
    ````
    http://127.0.0.1:5984/_utils
    ````
4. Access node dirrecty in container
* Get container id `sudo docker ps`
* Connect to container `sudo docker exec -it <id> bash`
* Nodes are located at `/usr/src/couchdb/dev/lib/nodeN`
     

Setting Keycloak server
-----------------------

Setting EAP server
------------------

Build application
-----------------

