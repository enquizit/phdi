IF DB_ID('record_linkage') IS NULL
  CREATE DATABASE record_linkage;
  GO
  USE record_linkage;
  GO
  CREATE SCHEMA cdc;
  GO