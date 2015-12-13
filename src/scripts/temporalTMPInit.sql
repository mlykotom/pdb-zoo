DROP SEQUENCE Employees_seq;
DROP SEQUENCE Employees_Shift_seq;
DROP SEQUENCE Animals_seq;
DROP SEQUENCE Animals_Records_seq;

DROP TABLE Employees;
DROP TABLE Animals;
DROP TABLE Employees_Shift;
DROP TABLE Animals_Records;

CREATE TABLE Employees (
  ID INT NOT NULL,
  Name VARCHAR(255) NOT NULL,
  Surname VARCHAR (255) NOT NULL,
  PRIMARY KEY (ID)
);

CREATE TABLE Animals (
  ID INT NOT NULL,
  Name VARCHAR(255) NOT NULL,
  Species VARCHAR (255) NOT NULL,
  PRIMARY KEY (ID)
);

CREATE TABLE Employees_Shift (
  ID INT NOT NULL,
  EmplID INT NOT NULL,
  Location INT,
  dFrom DATE NOT NULL,
  dTo DATE NOT NULL,
  PRIMARY KEY (ID),
  FOREIGN KEY (Location) REFERENCES Spatial_Objects (ID),
  FOREIGN KEY (EmplID) REFERENCES Employees (ID)
);

CREATE TABLE Animals_Records (
  ID INT NOT NULL,
  AnimalID INT NOT NULL,
  Location INT,
  Weight NUMBER,
  dFrom DATE NOT NULL,
  dTo DATE NOT NULL,
  PRIMARY KEY (ID),
  FOREIGN KEY (Location) REFERENCES Spatial_Objects (ID),
  FOREIGN KEY (AnimalID) REFERENCES Animals (ID)
);

CREATE SEQUENCE Employees_seq;
CREATE OR REPLACE TRIGGER Employees_bir
BEFORE INSERT ON Employees
FOR EACH ROW
  BEGIN
    SELECT Employees_seq.NEXTVAL
    INTO :new.id
    FROM dual;
  END;
/

CREATE SEQUENCE Employees_Shift_seq;
CREATE OR REPLACE TRIGGER Employees_Shift_bir
BEFORE INSERT ON Employees_Shift
FOR EACH ROW
  BEGIN
    SELECT Employees_Shift_seq.NEXTVAL
    INTO :new.id
    FROM dual;
  END;
/

CREATE SEQUENCE Animals_seq;
CREATE OR REPLACE TRIGGER Animals_bir
BEFORE INSERT ON Animals
FOR EACH ROW
  BEGIN
    SELECT Animals_seq.NEXTVAL
    INTO :new.id
    FROM dual;
  END;
/

select * from Animals_Records;

CREATE SEQUENCE Animals_Records_seq;
CREATE OR REPLACE TRIGGER Animals_Records_bir
BEFORE INSERT ON Animals_Records
FOR EACH ROW
  BEGIN
    SELECT Animals_Records_seq.NEXTVAL
    INTO :new.id
    FROM dual;
  END;
/

COMMIT;

INSERT INTO Employees_Shift ( EmplId, Location, dFrom, dTo ) VALUES (1, 2, '01-Jan-2000', '01-Jan-2500');
INSERT INTO Employees_Shift ( EmplId, Location, dFrom, dTo ) VALUES (2, 2, '01-Jan-1999', '01-Jan-2014');
INSERT INTO Employees_Shift ( EmplId, Location, dFrom, dTo ) VALUES (2, 7, '01-Jan-2014', '01-Jan-2500');
INSERT INTO Employees_Shift ( EmplId, Location, dFrom, dTo ) VALUES (3, 7, '01-Jan-2000', '01-Jan-2500');
INSERT INTO Employees_Shift ( EmplId, Location, dFrom, dTo ) VALUES (4, 2, '01-Jan-2000', '01-Jan-2500');

INSERT INTO Employees (Name, Surname) VALUES ('Jack', 'Daniels');
INSERT INTO Employees (Name, Surname) VALUES ('Jarko', 'Mustafa');
INSERT INTO Employees (Name, Surname) VALUES ('Pa', 'Pi');
INSERT INTO Employees (Name, Surname) VALUES ('Radost', 'Zit');
INSERT INTO Employees (Name, Surname) VALUES ('John', 'McClane');

INSERT INTO Employees_Shift ( EmplId, Location, dFrom, dTo ) VALUES (1, 2, '01-Jan-2000', '01-Jan-2500');
INSERT INTO Employees_Shift ( EmplId, Location, dFrom, dTo ) VALUES (2, 2, '01-Jan-1999', '01-Jan-2014');
INSERT INTO Employees_Shift ( EmplId, Location, dFrom, dTo ) VALUES (2, 7, '01-Jan-2014', '01-Jan-2500');
INSERT INTO Employees_Shift ( EmplId, Location, dFrom, dTo ) VALUES (3, 7, '01-Jan-2000', '01-Jan-2500');
INSERT INTO Employees_Shift ( EmplId, Location, dFrom, dTo ) VALUES (4, 2, '01-Jan-2000', '01-Jan-2500');

INSERT INTO Animals (Name, Species) VALUES ('Tomas', 'Horse');
INSERT INTO Animals (Name, Species) VALUES ('Gizela', 'Gazelle');
INSERT INTO Animals (Name, Species) VALUES ('Jasmine', 'Goat');
INSERT INTO Animals (Name, Species) VALUES ('Yogi', 'Bear');
INSERT INTO Animals (Name, Species) VALUES ('Bubu', 'Bear');
INSERT INTO Animals (Name, Species) VALUES ('Tweety', 'Bird');
INSERT INTO Animals (Name, Species) VALUES ('Donald', 'Duck');

INSERT INTO Animals_Records ( AnimalId, Location, Weight, dFrom, dTo ) VALUES (1, 2, 150.35, '01-Jan-2000', '01-Jan-2500');
INSERT INTO Animals_Records ( AnimalId, Location, Weight, dFrom, dTo ) VALUES (2, 2, 200.45, '01-Jan-1999', '01-Jan-2014');
INSERT INTO Animals_Records ( AnimalId, Location, Weight, dFrom, dTo ) VALUES (2, 7, 195.45, '01-Jan-2014', '01-Jan-2500');
INSERT INTO Animals_Records ( AnimalId, Location, Weight, dFrom, dTo ) VALUES (3, 7, 60.45, '01-Jan-2000', '01-Jan-2500');
INSERT INTO Animals_Records ( AnimalId, Location, Weight, dFrom, dTo ) VALUES (4, 2, 650.45, '01-Jan-2000', '01-Jan-2500');
INSERT INTO Animals_Records ( AnimalId, Location, Weight, dFrom, dTo ) VALUES (5, 2, 250.49, '01-Jan-2000', '01-Jan-2500');
INSERT INTO Animals_Records ( AnimalId, Location, Weight, dFrom, dTo ) VALUES (6, 2, 1.32, '01-Jan-2000', '01-Jan-2500');
INSERT INTO Animals_Records ( AnimalId, Location, Weight, dFrom, dTo ) VALUES (7, 2, 6.5, '01-Jan-2000', '01-Jan-2500');


SELECT * FROM Animals_Records
SELECT e.*, s.Location, s.dFrom, s.dTo  FROM Employees e
  LEFT JOIN Employees_Shift s on e.ID = s.EmplID
WHERE s.dFrom <= dateToShow and s.dTo >= dateToShow


SELECT * FROM Employees

SELECT * FROM Employees_Shift

SELECT e.ID, e.Name, e.Surname, Location FROM EMPLOYEES e JOIN Employees_Shift s ON e.ID = s.EmplId

SELECT * FROM Animals_Records;

CREATE OR REPLACE PROCEDURE deleteEmployeeShiftTable(p_animalID IN INT, p_dateFrom IN Date, p_dateTo IN Date) IS
  animalID INT;
  dateFrom DATE;
  dateTo DATE;

  isUpdated BOOLEAN;

  BEGIN
    animalID := p_animalID;
    dateFrom := p_dateFrom;
    dateTo := p_dateTo;

    FOR rec IN (SELECT * FROM EMPLOYEES_SHIFT sh WHERE sh.EmplID = animalID AND (dFrom <= dateTo AND dTo >= dateFrom))
    LOOP
      isUpdated := FALSE;
      IF (rec.DFROM < dateFrom) THEN
        -- UPDATE rec
        UPDATE Employees_Shift
        SET dTo = dateFrom -1
        WHERE ID = rec.ID;

        rec.dFrom := dateFrom;

        isUpdated := TRUE;
      END IF;

      IF (rec.dFrom >= dateFrom AND rec.dTo <= dateTo) THEN
        IF ( isUpdated = FALSE) THEN
          DELETE Employees_Shift WHERE ID = rec.ID;
        END IF;
      ELSE
        rec.dFrom := dateTo + 1;
        --  ULOZ REC (INSERT)
        IF (isUpdated = TRUE) THEN
          INSERT INTO Employees_Shift ( EmplId, Location, dFrom, dTo )
          VALUES (animalID, rec.Location, rec.dFrom, rec.dTo );
        ELSE
          UPDATE Employees_Shift
          SET dTo = rec.dTo, dFrom = rec.dFrom
          WHERE ID = rec.ID;
        END IF;
      END IF;
    END LOOP;
  end;
/


COMMIT;

SELECT * FROM Employees_Shift where EmplID = 41;

ROLLBACK;

DECLARE
  v_DateFrom date := to_date('01-JAN-1974', 'DD-Mon-YYYY');
  v_DateTo date := to_date('01-JAN-1990', 'DD-Mon-YYYY');
  v_EmployeeID INT := 41;
BEGIN
  deleteEmployeeShiftTable(v_EmployeeID, v_DateFrom, v_DateTo);
END;

CREATE OR REPLACE PROCEDURE updateEmployeeTable(p_animalID IN INT, p_dateFrom IN Date, p_dateTo IN Date, p_new_location in INT) IS
animalID INT;
dateFrom DATE;
dateTo DATE;
new_location INT;

isUpdated BOOLEAN;

A_OLD_FROM DATE;
A_OLD_TO DATE;
A_OLD_LOC INT;
BEGIN
  animalID := p_animalID;
  dateFrom := p_dateFrom;
  dateTo := p_dateTo;
  new_location := p_new_location;


  FOR rec IN (SELECT * FROM EMPLOYEES_SHIFT sh WHERE sh.EmplID = animalID AND (dFrom <= dateTo AND dTo >= dateFrom) ORDER BY sh.dFrom)
  LOOP
    isUpdated := FALSE;

    IF (rec.DFROM < dateFrom) THEN
      IF (rec.Location = new_location) THEN
        dateFrom := rec.dFrom;
      ELSE
        A_OLD_FROM := rec.dFrom;
        A_OLD_LOC := rec.Location;
        A_OLD_TO := dateFrom -1;

        -- INSERT A_OLD TODO only UPDATE ... do note create A_OLD...
        UPDATE Employees_Shift
        SET dTo = A_OLD_TO, dFrom = A_OLD_FROM, Location = A_OLD_LOC
      WHERE ID = rec.ID;

        rec.dFrom := dateFrom;
        isUpdated := TRUE;
      END IF;
    END IF;
    IF (rec.dTo <= dateTo) THEN
      -- DELETE rec
      if (isUpdated = FALSE) THEN
        DELETE Employees_Shift WHERE ID = rec.ID;
      END IF;
    ELSE
      IF (rec.Location = new_location) THEN
        dateTo := rec.dTo;
        -- DELETE rec
        DELETE Employees_Shift WHERE ID = rec.ID;
      ELSE
        ----- Case 3 DIAGRAM
        if (isUpdated = FALSE) THEN
          DELETE Employees_Shift WHERE ID = rec.ID;
        END IF;
        ------
        rec.dFrom := dateTo + 1;
        --  ULOZ REC (INSERT)
        INSERT INTO Employees_Shift ( EmplId, Location, dFrom, dTo )
      VALUES (animalID, rec.Location, rec.dFrom, rec.dTo );
      END IF;
    END IF;
  END LOOP;
  -- INSERT NEW FROM ARGUMENTS
  INSERT INTO Employees_Shift (EmplID, Location, dFrom, dTo)
  VALUES (animalID, new_location, dateFrom, dateTo);

end;
/


DECLARE
v_DateFrom date := to_date('01-JAN-1975', 'DD-Mon-YYYY');
v_DateTo date := to_date('01-JAN-2000', 'DD-Mon-YYYY');
v_EmployeeID INT := 41;
v_Location INT := 7;
BEGIN
  updateEmployeeTable(v_EmployeeID, v_DateFrom, v_DateTo, v_Location);
END;

SELECT * FROM Animals_Records


DELETE Employees_Shift WHERE ID = 179;

INSERT INTO Employees_Shift ( EmplId, Location, dFrom, dTo ) VALUES (41, 4, '01-JAN-1990', '01-JAN-2000');
INSERT INTO Employees_Shift ( EmplId, Location, dFrom, dTo ) VALUES (41, 4, '01-JAN-1975', '01-JAN-1980');


SELECT * FROM EMPLOYEES_SHIFT sh WHERE sh.EmplID = 41 AND ((dFrom <= '01-JAN-1990') AND dTo >= '01-JAN-200');

102	1989-12-09	1994-12-28	#4 Entrance: ctvrty_objekt

INSERT INTO Employees_Shift ( EmplId, Location, dFrom, dTo ) VALUES (28, 2, '01-Jan-1995', '01-Jan-1998');

INSERT INTO Employees_Shift ( EmplId, Location, dFrom, dTo ) VALUES (28, 2, '01-Jan-1990', '01-Jan-1993');



CREATE TABLE Employees_ShiftTemporal (
ID INT NOT NULL,
EmplID INT NOT NULL,
Location INT,
dFrom DATE NOT NULL,
dTo DATE NOT NULL,
PRIMARY KEY (ID),
FOREIGN KEY (Location) REFERENCES Spatial_Objects (ID),
FOREIGN KEY (EmplID) REFERENCES Employees (ID)
);


-- CREATE OR REPLACE PROCEDURE updateEmployeeTable(dateFrom IN Date, dateTo IN Date, animalID IN LONG, new_location in LONG) IS

commit;



SELECT * FROM Employees_Shift WHERE EmplID = 28

SELECT * FROM Employees_Shift where EmplID = 41;

2500-01-01 00:00:00
DELETE Employees_Shift WHERE ID in (81,83)
UPDATE Employees_Shift SET dTo = '01-Jan-2500' WHERE ID = 1

SELECT Name FROM Animals

commit;

ALTER TABLE Animals
ADD ( photo ORDSYS.ORDImage, photo_si ORDSYS.SI_StillImage ,
photo_ac ORDSYS.SI_AverageColor , photo_ch ORDSYS.SI_ColorHistogram ,
photo_pc ORDSYS.SI_PositionalColor , photo_tx ORDSYS.SI_Texture);

              photo ORDSYS.ORDImage, photo_si ORDSYS.SI_StillImage ,
photo_ac ORDSYS.SI_AverageColor , photo_ch ORDSYS.SI_ColorHistogram ,
photo_pc ORDSYS.SI_PositionalColor , photo_tx ORDSYS.SI_Texture


COMMIT;

ROLLBACK;

SELECT * FROM Animals_Records;

DECLARE
  v_DateFrom date := to_date('01-JAN-1970', 'DD-Mon-YYYY');
  v_DateTo date := to_date('01-JAN-1975', 'DD-Mon-YYYY');
  v_animalID INT := 1;
  v_Location INT := 2;
  v_Weight NUMBER := 10.35;
BEGIN
  updateAnimalTable(v_animalID, v_DateFrom, v_DateTo, v_Location, v_Weight);
END;

SELECT a.ID as AnimalID, a.name, a.Species, s.ID as ShiftID, s.Location, s.Weight, s.dFrom, s.dTo as DO FROM Animals a LEFT JOIN Animals_Records s ON a.ID = s.AnimalID WHERE s.Weight <> NULL;


CREATE OR REPLACE PROCEDURE updateAnimalTable(p_animalID IN INT, p_dateFrom IN Date, p_dateTo IN Date, p_new_location in INT, p_new_weight in NUMBER) IS
new_animalID INT;
dateFrom DATE;
dateTo DATE;
new_location INT;
new_weight NUMBER;

isUpdated BOOLEAN;

A_OLD_FROM DATE;
A_OLD_TO DATE;
A_OLD_LOC INT;
BEGIN
  new_animalID := p_animalID;
  dateFrom := p_dateFrom;
  dateTo := p_dateTo;
  new_location := p_new_location;
  new_weight := p_new_weight;

  FOR rec IN (SELECT * FROM Animals_Records sh WHERE sh.AnimalID = new_animalID AND (sh.dFrom <= dateTo AND sh.dTo >= dateFrom) ORDER BY sh.dFrom)
  LOOP
    isUpdated := FALSE;

    IF (rec.DFROM < dateFrom) THEN
      IF (rec.Location = new_location AND rec.Weight = new_weight) THEN
        dateFrom := rec.dFrom;
      ELSE
        A_OLD_FROM := rec.dFrom;
        A_OLD_LOC := rec.Location;
        A_OLD_TO := dateFrom -1;

        -- INSERT A_OLD TODO only UPDATE ... do note create A_OLD...
        UPDATE Animals_Records
        SET dTo = A_OLD_TO, dFrom = A_OLD_FROM, Location = A_OLD_LOC
        WHERE ID = rec.ID;

        rec.dFrom := dateFrom;
        isUpdated := TRUE;
      END IF;
    END IF;
    IF (rec.dTo <= dateTo) THEN
      -- DELETE rec
      if (isUpdated = FALSE) THEN
        DELETE Animals_Records WHERE ID = rec.ID;
      END IF;
    ELSE
      IF (rec.Location = new_location AND rec.Weight = new_weight) THEN
        dateTo := rec.dTo;
        -- DELETE rec
        DELETE Animals_Records WHERE ID = rec.ID;
      ELSE
        ----- Case 3 DIAGRAM
        if (isUpdated = FALSE) THEN
          DELETE Animals_Records WHERE ID = rec.ID;
        END IF;
        ------
        rec.dFrom := dateTo + 1;
        --  ULOZ REC (INSERT)
        INSERT INTO Animals_Records ( AnimalID, Location, Weight, dFrom, dTo )
        VALUES (new_animalID, rec.Location, rec.Weight, rec.dFrom, rec.dTo );
      END IF;
    END IF;
  END LOOP;
  -- INSERT NEW FROM ARGUMENTS
  INSERT INTO Animals_Records (AnimalID, Location, Weight, dFrom, dTo)
  VALUES (new_animalID, new_location, new_weight, dateFrom, dateTo);

end;

-- --------------------------------------------
-- -         UPDATE ANIMALS_RECORDS TABLE    --
-- --------------------------------------------

CREATE OR REPLACE PROCEDURE deleteAnimalsRecordsTable(p_animalID IN INT, p_dateFrom IN Date, p_dateTo IN Date) IS
  new_animalID INT;
  dateFrom DATE;
  dateTo DATE;

  isUpdated BOOLEAN;

  BEGIN
    new_animalID := p_animalID;
    dateFrom := p_dateFrom;
    dateTo := p_dateTo;
    isUpdated := FALSE;

    FOR rec IN (SELECT * FROM Animals_Records ar WHERE ar.AnimalID = new_animalID AND (ar.dFrom <= dateTo AND ar.dTo >= dateFrom))
    LOOP
      IF (rec.DFROM < dateFrom) THEN
        -- UPDATE rec
        UPDATE Animals_Records
        SET dTo = dateFrom -1
        WHERE ID = rec.ID;

        rec.dFrom := dateFrom;

        isUpdated := TRUE;
      END IF;

      IF (rec.dFrom >= dateFrom AND rec.dTo <= dateTo) THEN
        IF ( isUpdated = FALSE) THEN
          DELETE Animals_Records WHERE ID = rec.ID;
        END IF;
      ELSE
        rec.dFrom := dateTo + 1;
        --  ULOZ REC (INSERT)
        IF (isUpdated = TRUE) THEN
          INSERT INTO Animals_Records ( AnimalID, Location, Weight, dFrom, dTo )
          VALUES (new_animalID, rec.Location, rec.Weight, rec.dFrom, rec.dTo );
        ELSE
          UPDATE Animals_Records
          SET dTo = rec.dTo, dFrom = rec.dFrom
          WHERE ID = rec.ID;
        END IF;
      END IF;
    END LOOP;
  end;
/

COMMIT;

ROLLBACK;

SELECT * FROM Animals_Records where AnimalID = 34;

DECLARE
  v_DateFrom date := to_date('01-JAN-2001', 'DD-Mon-YYYY');
  v_DateTo date := to_date('01-JAN-2010', 'DD-Mon-YYYY');
  v_animalID INT := 34;
BEGIN
  deleteAnimalsRecordsTable(v_animalID, v_DateFrom, v_DateTo);
END;




