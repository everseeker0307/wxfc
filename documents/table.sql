create database if not exists wxfc;
alter database wxfc character set utf8;
use wxfc;

create table if not exists houseinfo(id int NOT NULL auto_increment primary key, houseUrlId varchar(16), houseName varchar(128), tempName varchar(256), presellLicence varchar(128), presellApproval varchar(128), houseDeveloper varchar(128), partner varchar(128), location varchar(256),
adminRegion varchar(64), approval varchar(128), planLicence varchar(128), landLicence varchar(128), constructLicence varchar(128), holdLandLicence varchar(128),
presellArea float(12, 3), sellCompany varchar(64), sellTel varchar(64), sellAddress varchar(128), telephone varchar(64),
tenement varchar(128), totalHouseNum int, createDate varchar(10), UNIQUE(houseUrlId));

create table if not exists housestock(id int NOT NULL auto_increment primary key, houseUrlId varchar(16), totalHouseNum int, forsaleHouseNum int, saledHouseNum int,
limitedHouseNum int, recordDate varchar(10));
alter table housestock add unique index(houseUrlId, recordDate);