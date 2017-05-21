# Relax DMS

This is my master thesis at BUT FIT. It's implementation of distributed document server based on CouchDB database.

Setting CouchDB cluster
-----------------------

Cluster is available through Docker image [klaemo/couchdb:2.0-dev](https://github.com/klaemo/docker-couchdb). There are 3 configured CouchDB nodes wiht HAProxy in front them.
All you need is Docker installed on your machine.

1. Start Docker

    ```
    sudo systemctl start docker
    ``` 
2. Run Docker container

    ```
    sudo docker run --privileged=true -it -p 5984:5984 -v $(pwd)/couchdb:/usr/src/couchdb/dev/lib/node1/data klaemo/couchdb:2.0-dev --admin=admin:password --with-haproxy
    ```

    Note this will mount data from Docker CouchDB cluster to your host specifically to your current working directory.

3. Access cluster through proxy at port 5984 [http://127.0.0.1:5984/_utils](http://127.0.0.1:5984/_utils)


Access CouchDB cluster
----------------------

To access node dirrecty in the Docker container do following:

* Get container id `sudo docker ps`
* Connect to the container `sudo docker exec -it <id> bash`
* Nodes are located at `/usr/src/couchdb/dev/lib/nodeN`
     

Setting Keycloak server
-----------------------
Download [Keycloak server 1.9.8.Final](https://downloads.jboss.org/keycloak/1.9.8.Final/keycloak-1.9.8.Final.zip) and unzip it. To import all needed settings, download [importKeycloak.json](https://github.com/martin-kanis/relax-dms/blob/master/importKeycloak.json) file and run this command from bin directory (specify path to `importKeycloak.json`):

```
./standalone.sh -Djboss.socket.binding.port-offset=100 -Dkeycloak.migration.action=import -Dkeycloak.migration.provider=singleFile -Dkeycloak.migration.file=importKeycloak.json -Dkeycloak.migration.strategy=OVERWRITE_EXISTING
```

To run Keycloak server without importing go to bin directory and run following command that starts Keycloak on port 8180:

```
./standalone.sh -Djboss.socket.binding.port-offset=100
```

Administration console is then available on [http://localhost:8180/auth/admin](http://localhost:8180/auth/admin) address (Username is `admin` and password is `admin`):


Setting EAP server
------------------
Download [EAP 7.0.0](https://developers.redhat.com/download-manager/file/jboss-eap-7.0.0.zip) and unzip it. Download [BRMS 6.4](https://developers.redhat.com/download-manager/content/origin/files/sha256/14/148eb9be40833d5da00bb6108cbed1852924135d25ceb6c601c62ba43f99f372/jboss-brms-6.4.0.GA-deployable-eap7.x.zip) deployable for EAP 7 and unzip it in your EAP 7 directory. To start EAP server run following command in EAP 7 bin directory:

```
./standalone.sh
```

Access BRMS server
------------------
Business central console is deployed on [http://localhost:8080/business-central](http://localhost:8080/business-central). Username is `bpmsAdmin` and password is `password`.

Build application
-----------------
You will need `maven 3.1.1` or later and added redhat GA repository to your `settings.xml` in order to build application. Download [settingsBXMS.xml](https://github.com/martin-kanis/relax-dms/blob/master/settingsBXMS.xml) file and then run following command with correct path to the file:

```
mvn -s /path/to/settingsBXMS.xml clean install
```
Deploy application
------------------
To deploy application to EAP server copy the `relax-dms-*.ear` file to EAP deployments folder:

```
cp application/target/relax-dms*.ear $eapDir/standalone/deployments
```

Rest API
--------
For access REST endpoint you will need access token for authentication. You can obtain the access token for user `writer` with following command:

```
curl --data "grant_type=password&client_id=relax-dms&username=writer&password=password" http://localhost:8180/auth/realms/RelaxDMS/protocol/openid-connect/token
```

