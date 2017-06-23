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
alter table housestock add index (recordDate);

select (r1 - r0) as r, t.houseUrlId  from (select t1.saledHouseNum r1, t0.saledHouseNum r0, t1.houseUrlId from housestock t1 join housestock t0 on t1.houseUrlId = t0.houseUrlId and t1.recordDate='20170402' and t0.recordDate='20170401') as t order by r desc;
select h.houseName, sum(s.r) as dealNum from houseinfo h join (select (r1 - r0) as r, t.houseUrlId from (select t1.saledHouseNum r1, t0.saledHouseNum r0, t1.houseUrlId from housestock t1 join housestock t0 on t1.houseUrlId = t0.houseUrlId and t1.recordDate='20170401' and t0.recordDate='20170331') as t having r != 0) as s on s.houseUrlId=h.houseUrlId group by h.houseName order by dealNum desc;
select sum(saledHouseNum) from housestock where recordDate='20170421';
select sum(forsaleHouseNum) from housestock where recordDate='20170421';
select sum(limitedHouseNum) from housestock where recordDate='20170327';
