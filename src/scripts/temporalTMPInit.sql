DROP SEQUENCE Employees_seq;
DROP SEQUENCE Employees_Shift_seq;

DROP TABLE Employees;

DROP TABLE Employees_Shift;

CREATE TABLE Employees (
  ID INT NOT NULL,
  Name VARCHAR(255) NOT NULL,
  Surname VARCHAR (255) NOT NULL,
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

commit

SELECT e.*, s.Location, s.dFrom, s.dTo  FROM Employees e
  LEFT JOIN Employees_Shift s on e.ID = s.EmplID
WHERE s.dFrom <= dateToShow and s.dTo >= dateToShow


SELECT * FROM Employees

SELECT * FROM Employees_Shift

SELECT e.ID, e.Name, e.Surname, Location FROM EMPLOYEES e JOIN Employees_Shift s ON e.ID = s.EmplId

SELECT * FROM Employees_Shift

CREATE OR REPLACE PROCEDURE deleteEmployeeShiftTable(p_employeeID IN INT, p_dateFrom IN Date, p_dateTo IN Date) IS
  employeeID INT;
  dateFrom DATE;
  dateTo DATE;

  isUpdated BOOLEAN;
  isDeleted BOOLEAN;

  A_OLD_FROM DATE;
  A_OLD_TO DATE;
  BEGIN
    employeeID := p_employeeID;
    dateFrom := p_dateFrom;
    dateTo := p_dateTo;
    isUpdated := FALSE;

    FOR rec IN (SELECT * FROM EMPLOYEES_SHIFT sh WHERE sh.EmplID = employeeID AND (dFrom <= dateTo AND dTo >= dateFrom))
    LOOP
      isDeleted := FALSE;
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
          VALUES (employeeID, rec.Location, rec.dFrom, rec.dTo );
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

CREATE OR REPLACE PROCEDURE updateEmployeeTable(p_employeeID IN INT, p_dateFrom IN Date, p_dateTo IN Date, p_new_location in INT) IS
employeeID INT;
dateFrom DATE;
dateTo DATE;
new_location INT;

isUpdated BOOLEAN;

A_OLD_FROM DATE;
A_OLD_TO DATE;
A_OLD_LOC INT;
BEGIN
  employeeID := p_employeeID;
  dateFrom := p_dateFrom;
  dateTo := p_dateTo;
  new_location := p_new_location;


  FOR rec IN (SELECT * FROM EMPLOYEES_SHIFT sh WHERE sh.EmplID = employeeID AND (dFrom <= dateTo AND dTo >= dateFrom) ORDER BY sh.dFrom)
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
      VALUES (employeeID, rec.Location, rec.dFrom, rec.dTo );
      END IF;
    END IF;
  END LOOP;
  -- INSERT NEW FROM ARGUMENTS
  INSERT INTO Employees_Shift (EmplID, Location, dFrom, dTo)
  VALUES (employeeID, new_location, dateFrom, dateTo);

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


-- CREATE OR REPLACE PROCEDURE updateEmployeeTable(dateFrom IN Date, dateTo IN Date, employeeID IN LONG, new_location in LONG) IS

commit


SELECT * FROM Employees_Shift WHERE EmplID = 28

SELECT * FROM Employees_Shift where EmplID = 41;

2500-01-01 00:00:00
DELETE Employees_Shift WHERE ID in (81,83)
UPDATE Employees_Shift SET dTo = '01-Jan-2500' WHERE ID = 1

SELECT * FROM SPATIAL_OBJECTS