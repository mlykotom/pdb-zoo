-- insert spatial object types
INSERT INTO SPATIAL_OBJECT_TYPES (NAME, COLOR) VALUES ('Enclosure', '309420');
INSERT INTO SPATIAL_OBJECT_TYPES (NAME, COLOR) VALUES ('Restaurant', 'FF00FF');
INSERT INTO SPATIAL_OBJECT_TYPES (NAME, COLOR) VALUES ('Entrance', 'A92CD8');
INSERT INTO SPATIAL_OBJECT_TYPES (NAME, COLOR) VALUES ('Water', '2BA8D2');
INSERT INTO SPATIAL_OBJECT_TYPES (NAME, COLOR) VALUES ('Tree', '96D22B');
INSERT INTO SPATIAL_OBJECT_TYPES (NAME, COLOR) VALUES ('Toilets', '945727');
INSERT INTO SPATIAL_OBJECT_TYPES (NAME, COLOR) VALUES ('Path', '141823');

-- insert spatial data
INSERT INTO SPATIAL_OBJECTS (NAME, TYPE, GEOMETRY, ZINDEX) VALUES ('Bunch of trees', 6, SDO_GEOMETRY(2005,null,null,SDO_ELEM_INFO_ARRAY(1,1,7),SDO_ORDINATE_ARRAY(442,262,218,131,300,62,625,389,309,441,117,357,76,236)), '0');
INSERT INTO SPATIAL_OBJECTS (NAME, TYPE, GEOMETRY, ZINDEX) VALUES ('Path 1', 7, SDO_GEOMETRY(2002,null,null,SDO_ELEM_INFO_ARRAY(1,2,1),SDO_ORDINATE_ARRAY(72.181049153838,55.8988957619063,76.1610736924623,198.184773017725,193.571797581879,195.199754613757,195.561809851191,323.555545984391,240.337085910715,414.101104238094,401.528079724999,410.121079699469,517.94379747976,360.370772966666,542.818950846162,191.219730075133,376.652926358597,236.990012269312,304.017478528704,193.209742344445,196.556815985847,196.194760748413,194.566803716535,196.194760748413)), '0');
INSERT INTO SPATIAL_OBJECTS (NAME, TYPE, GEOMETRY, ZINDEX) VALUES ('Path 2', 7, SDO_GEOMETRY(2002,null,null,SDO_ELEM_INFO_ARRAY(1,2,1),SDO_ORDINATE_ARRAY(191,268,49,278,56,413,152,437,221,379)), '0');
INSERT INTO SPATIAL_OBJECTS (NAME, TYPE, GEOMETRY, ZINDEX) VALUES ('Bears', 1, SDO_GEOMETRY(2003,null,null,SDO_ELEM_INFO_ARRAY(1,1003,1),SDO_ORDINATE_ARRAY(224,367,251,336,295,353,278,308,205,294,201,322,224,367)), '0');
INSERT INTO SPATIAL_OBJECTS (NAME, TYPE, GEOMETRY, ZINDEX) VALUES ('Path 3', 7, SDO_GEOMETRY(2002,null,null,SDO_ELEM_INFO_ARRAY(1,2,1),SDO_ORDINATE_ARRAY(420.076592083817,404.529561343831,495.487582290537,443.177693824775,586.923407916183,441.292419069607,582.210221028263,307.437911452681,535.078352149064,251.822306175225)), '0');
INSERT INTO SPATIAL_OBJECTS (NAME, TYPE, GEOMETRY, ZINDEX) VALUES ('Restaurant with park', 2, SDO_GEOMETRY(2007,null,null,SDO_ELEM_INFO_ARRAY(1,1003,3,5,2003,4),SDO_ORDINATE_ARRAY(199.348078658846,200.443900113301,288.651921046598,243.556099886699,252.70997951319,230.709979660468,235.290020192254,230.709979660468,243.999999852722,209.682228636172)), '1');
INSERT INTO SPATIAL_OBJECTS (NAME, TYPE, GEOMETRY, ZINDEX) VALUES ('Toilets 1', 6, SDO_GEOMETRY(2003,null,null,SDO_ELEM_INFO_ARRAY(1,1003,3),SDO_ORDINATE_ARRAY(199,246,219,260)), '0');
INSERT INTO SPATIAL_OBJECTS (NAME, TYPE, GEOMETRY, ZINDEX) VALUES ('Toilets 3', 6, SDO_GEOMETRY(2003,null,null,SDO_ELEM_INFO_ARRAY(1,1003,3),SDO_ORDINATE_ARRAY(521,241,533,254)), '0');
INSERT INTO SPATIAL_OBJECTS (NAME, TYPE, GEOMETRY, ZINDEX) VALUES ('Giraffes', 1, SDO_GEOMETRY(2003,null,null,SDO_ELEM_INFO_ARRAY(1,1003,1),SDO_ORDINATE_ARRAY(248,408,226,371,252,343,299,361,323,406,248,408)), '0');
INSERT INTO SPATIAL_OBJECTS (NAME, TYPE, GEOMETRY, ZINDEX) VALUES ('Goats', 1, SDO_GEOMETRY(2003,null,null,SDO_ELEM_INFO_ARRAY(1,1003,1),SDO_ORDINATE_ARRAY(465,375,463,321,520,302,513,353,465,375)), '0');
INSERT INTO SPATIAL_OBJECTS (NAME, TYPE, GEOMETRY, ZINDEX) VALUES ('Restaurant near goats', 2, SDO_GEOMETRY(2003,null,null,SDO_ELEM_INFO_ARRAY(1,1003,1),SDO_ORDINATE_ARRAY(401,403,417,339,455,320,453,378,401,403)), '0');
INSERT INTO SPATIAL_OBJECTS (NAME, TYPE, GEOMETRY, ZINDEX) VALUES ('Another WC', 6, SDO_GEOMETRY(2003,null,null,SDO_ELEM_INFO_ARRAY(1,1003,3),SDO_ORDINATE_ARRAY(61,395,73,410)), '0');
INSERT INTO SPATIAL_OBJECTS (NAME, TYPE, GEOMETRY, ZINDEX) VALUES ('Secret whole in the fence', 3, SDO_GEOMETRY(2001,null,SDO_POINT_TYPE(19,318,null),null, null), '0');
INSERT INTO SPATIAL_OBJECTS (NAME, TYPE, GEOMETRY, ZINDEX) VALUES ('Path shorten', 7, SDO_GEOMETRY(2002,null,null,SDO_ELEM_INFO_ARRAY(1,2,1),SDO_ORDINATE_ARRAY(390,410,411,329,375,237)), '0');
INSERT INTO SPATIAL_OBJECTS (NAME, TYPE, GEOMETRY, ZINDEX) VALUES ('Horses', 1, SDO_GEOMETRY(2003,null,null,SDO_ELEM_INFO_ARRAY(1,1003,1),SDO_ORDINATE_ARRAY(325,403,385,399,399,348,356,338,299,361,325,403)), '0');
INSERT INTO SPATIAL_OBJECTS (NAME, TYPE, GEOMETRY, ZINDEX) VALUES ('Lake with turtles', 4, SDO_GEOMETRY(2003,null,null,SDO_ELEM_INFO_ARRAY(1,1003,4),SDO_ORDINATE_ARRAY(255.31391066985,231.31391066985,234.68608933015,231.31391066985,245,206.413927649593)), '0');
INSERT INTO SPATIAL_OBJECTS (NAME, TYPE, GEOMETRY, ZINDEX) VALUES ('Lake', 4, SDO_GEOMETRY(2003,null,null,SDO_ELEM_INFO_ARRAY(1,1003,4),SDO_ORDINATE_ARRAY(334.865605705801,374.865605705801,273.134394294199,374.865605705801,304,300.349441799997)), '1');
INSERT INTO SPATIAL_OBJECTS (NAME, TYPE, GEOMETRY, ZINDEX) VALUES ('Avenue of trees 1', 6, SDO_GEOMETRY(2005,null,null,SDO_ELEM_INFO_ARRAY(1,1,8),SDO_ORDINATE_ARRAY(63,67,63,80,63,95,64,108,63,121,63,134,63,149,64,163)), '0');
INSERT INTO SPATIAL_OBJECTS (NAME, TYPE, GEOMETRY, ZINDEX) VALUES ('Avenue of trees 2', 6, SDO_GEOMETRY(2005,null,null,SDO_ELEM_INFO_ARRAY(1,1,8),SDO_ORDINATE_ARRAY(81,67,81,82,82,96,83,108,83,121,83,133,82,150,83,163)), '0');
INSERT INTO SPATIAL_OBJECTS (NAME, TYPE, GEOMETRY, ZINDEX) VALUES ('Staff entrance', 3, SDO_GEOMETRY(2003,null,null,SDO_ELEM_INFO_ARRAY(1,1003,3),SDO_ORDINATE_ARRAY(597,218,612,231)), '1');
INSERT INTO SPATIAL_OBJECTS (NAME, TYPE, GEOMETRY, ZINDEX) VALUES ('Fence', null, SDO_GEOMETRY(2002,null,null,SDO_ELEM_INFO_ARRAY(1,2,1),SDO_ORDINATE_ARRAY(612,467,20,457,16,47,394,46,395,147,605,143,612,466)), '0');
INSERT INTO SPATIAL_OBJECTS (NAME, TYPE, GEOMETRY, ZINDEX) VALUES ('Back Door', 3, SDO_GEOMETRY(2003,null,null,SDO_ELEM_INFO_ARRAY(1,1003,3),SDO_ORDINATE_ARRAY(284,454,308,466)), '0');
INSERT INTO SPATIAL_OBJECTS (NAME, TYPE, GEOMETRY, ZINDEX) VALUES ('Main entrance', 3, SDO_GEOMETRY(2003,null,null,SDO_ELEM_INFO_ARRAY(1,1003,3),SDO_ORDINATE_ARRAY(60,36,82,59)), '0');
INSERT INTO SPATIAL_OBJECTS (NAME, TYPE, GEOMETRY, ZINDEX) VALUES ('Palm tree', 6, SDO_GEOMETRY(2001,null,SDO_POINT_TYPE(345,125,null),null, null), '0');
INSERT INTO SPATIAL_OBJECTS (NAME, TYPE, GEOMETRY, ZINDEX) VALUES ('Entrance main parking', 3, SDO_GEOMETRY(2003,null,null,SDO_ELEM_INFO_ARRAY(1,1003,4),SDO_ORDINATE_ARRAY(421.132745950422,156.132745950422,390.867254049578,156.132745950422,406,119.599065440967)), '1');
INSERT INTO SPATIAL_OBJECTS (NAME, TYPE, GEOMETRY, ZINDEX) VALUES ('Path from entrance', 7, SDO_GEOMETRY(2002,null,null,SDO_ELEM_INFO_ARRAY(1,2,1),SDO_ORDINATE_ARRAY(384,153,303,193)), '0');

INSERT INTO Employees (Name, Surname) VALUES ('Jack', 'Daniels');
INSERT INTO Employees (Name, Surname) VALUES ('Jarko', 'Mustafa');
INSERT INTO Employees (Name, Surname) VALUES ('Pa', 'Pi');
INSERT INTO Employees (Name, Surname) VALUES ('Radost', 'Zit');
INSERT INTO Employees (Name, Surname) VALUES ('John', 'McClane');


INSERT INTO Employees_Shift ( EmplId, Location, dFrom, dTo ) VALUES (1, 10, TO_DATE('01-01-2000', 'dd-mm-yyyy'), TO_DATE('01-01-2500', 'dd-mm-yyyy'));
INSERT INTO Employees_Shift ( EmplId, Location, dFrom, dTo ) VALUES (2, 10, TO_DATE('01-01-1999', 'dd-mm-yyyy'), TO_DATE('01-01-2014', 'dd-mm-yyyy'));
INSERT INTO Employees_Shift ( EmplId, Location, dFrom, dTo ) VALUES (2, 4, TO_DATE('02-01-2014', 'dd-mm-yyyy'), TO_DATE('01-01-2500', 'dd-mm-yyyy'));
INSERT INTO Employees_Shift ( EmplId, Location, dFrom, dTo ) VALUES (3, 9, TO_DATE('01-01-2000', 'dd-mm-yyyy'), TO_DATE('01-01-2500', 'dd-mm-yyyy'));
INSERT INTO Employees_Shift ( EmplId, Location, dFrom, dTo ) VALUES (4, 9, TO_DATE('01-01-2000', 'dd-mm-yyyy'), TO_DATE('01-01-2500', 'dd-mm-yyyy'));

INSERT INTO Animals (Name, Species) VALUES ('Tomas', 'Horse');
INSERT INTO Animals (Name, Species) VALUES ('Gizela', 'Goat');
INSERT INTO Animals (Name, Species) VALUES ('Jasmine', 'Goat');
INSERT INTO Animals (Name, Species) VALUES ('Yogi', 'Bear');
INSERT INTO Animals (Name, Species) VALUES ('Bubu', 'Bear');
INSERT INTO Animals (Name, Species) VALUES ('Tweety', 'Bird');
INSERT INTO Animals (Name, Species) VALUES ('Donald', 'Duck');

INSERT INTO Animals_Records ( AnimalId, Location, Weight, dFrom, dTo ) VALUES (1, 15, 150.35, TO_DATE('01-01-2000', 'dd-mm-yyyy'), TO_DATE('01-01-2500', 'dd-mm-yyyy'));
INSERT INTO Animals_Records ( AnimalId, Location, Weight, dFrom, dTo ) VALUES (2, 10, 200.45, TO_DATE('01-01-1999', 'dd-mm-yyyy'), TO_DATE('01-01-2014', 'dd-mm-yyyy'));
INSERT INTO Animals_Records ( AnimalId, Location, Weight, dFrom, dTo ) VALUES (2, 10, 195.45, TO_DATE('02-01-2014', 'dd-mm-yyyy'), TO_DATE('01-01-2500', 'dd-mm-yyyy'));
INSERT INTO Animals_Records ( AnimalId, Location, Weight, dFrom, dTo ) VALUES (3, 10, 60.45, TO_DATE('01-01-2000', 'dd-mm-yyyy'), TO_DATE('01-01-2500', 'dd-mm-yyyy'));
INSERT INTO Animals_Records ( AnimalId, Location, Weight, dFrom, dTo ) VALUES (4, 4, 650.45, TO_DATE('01-01-2000', 'dd-mm-yyyy'), TO_DATE('01-01-2500', 'dd-mm-yyyy'));
INSERT INTO Animals_Records ( AnimalId, Location, Weight, dFrom, dTo ) VALUES (5, 4, 250.49, TO_DATE('01-01-2000', 'dd-mm-yyyy'), TO_DATE('01-01-2500', 'dd-mm-yyyy'));
INSERT INTO Animals_Records ( AnimalId, Location, Weight, dFrom, dTo ) VALUES (6, 9, 1.32, TO_DATE('01-01-2000', 'dd-mm-yyyy'), TO_DATE('01-01-2500', 'dd-mm-yyyy'));
INSERT INTO Animals_Records ( AnimalId, Location, Weight, dFrom, dTo ) VALUES (7, 9, 6.5, TO_DATE('01-01-2000', 'dd-mm-yyyy'), TO_DATE('01-01-2500', 'dd-mm-yyyy'));