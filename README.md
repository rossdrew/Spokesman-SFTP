#SFTPSpokesman

An SFTP Server which -when connected to- provides access to Amazon S3 buckets

### Usage

Specify port Start as Java process

```
mvn clean package spring-boot:run
```

then connect

```
sftp -P 21000 -i key.pem localhost
```

### Users & Access

 Users connect to SFTP using their ssh key.  At the moment, permitted keys are stored in [authorized_keys](https://github.com/evogirossdrew/sftpSpokesman/blob/master/src/main/resources/authorized_keys).
 The username provided is then used to limit which Amazon S3 bucket (`home`) is visible to that `user` via [application.yml](https://github.com/evogirossdrew/sftpSpokesman/blob/master/src/main/resources/application.yaml).  
 Amazon S3 access keys (__s3fs_access_key__ & __s3fs_secret_key__) need to be specified in [amazon.properties](https://github.com/evogirossdrew/sftpSpokesman/blob/master/src/main/resources/amazon.properties)
