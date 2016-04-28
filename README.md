#SFTPSpokesman

An SFTP Server which, when connected to, provides access to Amazon S3 buckets

#Usage

Start as Java process

```
mvn clean package spring-boot:run
```

then connect

```
sftp -P 21000 -i key.pem localhost
```