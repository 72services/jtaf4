docker run --name mariadb-jtaf -d -p3306:3306 -e MARIADB_ROOT_PASSWORD=my-secret-pw -e MARIADB_DATABASE=jtaf4 -e MARIADB_USER=jtaf4 -e MARIADB_PASSWORD=jtaf420 -d mariadb:10.3.29
