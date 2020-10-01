-- --liquibase formatted sql
--
-- --changeset nxtoan:1
-- CREATE SEQUENCE "ECBOX"."MANUFACTURE_SEQUENCE"
--  START WITH     1
--  INCREMENT BY   1
--  NOCACHE
--  NOCYCLE;
--
-- CREATE SEQUENCE "ECBOX"."BOXTYPE_SEQUENCE"
--  START WITH     1
--  INCREMENT BY   1
--  NOCACHE
--  NOCYCLE;
--
-- CREATE SEQUENCE "ECBOX"."BOX_SEQUENCE"
--  START WITH     1
--  INCREMENT BY   1
--  NOCACHE
--  NOCYCLE;
--
--  alter table ECBOX.TB_MANUFACTURE modify N_ID NUMBER default ECBOX.MANUFACTURE_SEQUENCE.nextval;
--