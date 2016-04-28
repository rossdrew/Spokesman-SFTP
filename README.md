#SFTPSpokesman

#Usage

Start as Java process

```
mvn clean package spring-boot:run
```

then connect

```
sftp -P 21000 -i key.pem localhost
```