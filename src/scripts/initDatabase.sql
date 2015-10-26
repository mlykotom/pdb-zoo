-- ----------------------------------------------------------------------------------------
-- DROPPING TABLES AND SEQUENCES
-- ----------------------------------------------------------------------------------------

DROP SEQUENCE Spatial_Object_Types_seq;
DROP SEQUENCE Spatial_Objects_seq;
DROP TABLE Spatial_Objects;
DROP TABLE Spatial_Object_Types;

-- ----------------------------------------------------------------------------------------
-- CREATING TABLES
-- ----------------------------------------------------------------------------------------

CREATE TABLE Spatial_Object_Types (
  ID    INT                       NOT NULL,
  Name  VARCHAR(255)              NOT NULL,
  Color VARCHAR(6) DEFAULT 000000 NOT NULL,
  PRIMARY KEY (ID)
);

CREATE TABLE Spatial_Objects (
  ID       INT          NOT NULL,
  Name     VARCHAR(255) NOT NULL,
  Type     INT          NOT NULL,
  Geometry SDO_GEOMETRY NOT NULL,

  PRIMARY KEY (ID),
  FOREIGN KEY (Type) REFERENCES Spatial_Object_Types (ID)
);

-- ----------------------------------------------------------------------------------------
-- CREATING SEQUENCES
-- ----------------------------------------------------------------------------------------

CREATE SEQUENCE Spatial_Object_Types_seq;
CREATE OR REPLACE TRIGGER Spatial_Object_Types_bir
BEFORE INSERT ON Spatial_Object_Types
FOR EACH ROW
  BEGIN
    SELECT Spatial_Object_Types_seq.NEXTVAL
    INTO :new.id
    FROM dual;
  END;
/

CREATE SEQUENCE Spatial_Objects_seq;
CREATE OR REPLACE TRIGGER Spatial_Objects_bir
BEFORE INSERT ON Spatial_Objects
FOR EACH ROW
  BEGIN
    SELECT Spatial_Objects_seq.NEXTVAL
    INTO :new.id
    FROM dual;
  END;
/

-- ----------------------------------------------------------------------------------------
-- INSERING DATA
-- ----------------------------------------------------------------------------------------

INSERT INTO Spatial_Object_Types (Name) VALUES ('Cage');
INSERT INTO Spatial_Object_Types (Name) VALUES ('Restaurant');
INSERT INTO Spatial_Object_Types (Name) VALUES ('Entry');

INSERT INTO Spatial_Objects (Name, Type, Geometry) VALUES (
  'prvni_objekt',
  1,
  SDO_GEOMETRY(2003, NULL, NULL,
               SDO_ELEM_INFO_ARRAY(1, 1003, 3),
               SDO_ORDINATE_ARRAY(50, 100, 80, 130)
  ));
INSERT INTO Spatial_Objects (Name, Type, Geometry) VALUES (
  'druhy_objekt',
  1,
  SDO_GEOMETRY(2003, NULL, NULL,
               SDO_ELEM_INFO_ARRAY(1, 1003, 3),
               SDO_ORDINATE_ARRAY(20, 200, 80, 260)
  ));
INSERT INTO Spatial_Objects (Name, Type, Geometry) VALUES (
  'treti_objekt',
  2,
  SDO_GEOMETRY(2003, NULL, NULL,
               SDO_ELEM_INFO_ARRAY(1, 1003, 3),
               SDO_ORDINATE_ARRAY(20, 35, 65, 50)
  ));

INSERT INTO Spatial_Objects (Name, Type, Geometry) VALUES (
  'ctvrty_objekt',
  3,
  SDO_GEOMETRY(2001, NULL,
               SDO_POINT_TYPE(300, 300, NULL),
               NULL, NULL)
);

INSERT INTO Spatial_Objects (Name, Type, Geometry) VALUES(
  'paty_objekt',
  3,
  SDO_GEOMETRY(2002, NULL, NULL,
      SDO_ELEM_INFO_ARRAY(1, 2, 1),
      SDO_ORDINATE_ARRAY(100, 100, 150,150, 300, 50)
  )
);

INSERT INTO Spatial_Objects (Name, Type, Geometry) VALUES(
  'curve_object',
  3,
  SDO_GEOMETRY(2002, NULL, NULL,
               SDO_ELEM_INFO_ARRAY(1, 2, 2),
               SDO_ORDINATE_ARRAY(400, 400, 250,250, 300, 50)
  )
);


INSERT INTO Spatial_Objects (Name, Type, Geometry) VALUES(
  'curve_object_2',
  3,
  SDO_GEOMETRY(2002, NULL, NULL,
               SDO_ELEM_INFO_ARRAY(1, 2, 2),
               SDO_ORDINATE_ARRAY(400, 400, 250,250, 300, 50)
  )
);

INSERT INTO Spatial_Objects (Name, Type, Geometry) VALUES (
  'circle',
    2,
    SDO_GEOMETRY(2003, NULL, NULL,
        SDO_ELEM_INFO_ARRAY(1, 1003, 4),
        SDO_ORDINATE_ARRAY(75,15, 75,95, 115,55)
  )
);

COMMIT;

-- ----------------------------------------------------------------------------------------
-- THE END OF SCRIPT
-- ----------------------------------------------------------------------------------------