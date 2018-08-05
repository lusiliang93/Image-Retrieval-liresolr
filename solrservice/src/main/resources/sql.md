# mysql用到的命令

## 启动、查看状态、关闭

- 启动
`bin/mysqld_safe --user=mysql &`

- 查看状态
`bin/mysqladmin version`
`bin/mysqladmin variables`

- 关闭
`bin/mysqladmin -u root shutdown`

## 创建用户

- 创建用户
`create user 'solr'@'localhost' identified by 'solr'`

- 授权
`grant all on *.* to 'solr'@'localhost'`
