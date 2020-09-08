### NCATS Omics Warehouse
This repository contains java code for the NCATS Omics Warehouse. The NOW is intended to warehouse multiple omics types, primarily holding processed data matrices and relevant metadata required to add context to datasets. The code includes data loading utilities and a dual-mode API that serves data via GraphQL conventions as well as REST. REST queries are limited to those that serve large data files rather than json. The NOW is built using Spring Boot to enable easy testing, adherence to an established technology and for quick, all in one deployment.
