docker run --name mysql-jtaf -d -p3306:3306 -e MYSQL_ROOT_PASSWORD=my-secret-pw -e MYSQL_DATABASE=jtaf4 -e MYSQL_USER=jtaf4 -e MYSQL_PASSWORD=jtaf420 -d mysql:5.7.34
