SET search_path TO public;

TRUNCATE TABLE student_violations, students, violations, rooms, contracts, dormitories RESTART IDENTITY CASCADE;

INSERT INTO public.contracts (id, number, start_date, end_date) VALUES (1, 1001, '2024-09-01', '2025-06-30');
INSERT INTO public.contracts (id, number, start_date, end_date) VALUES (2, 1003, '2024-09-01', '2025-06-30');
INSERT INTO public.contracts (id, number, start_date, end_date) VALUES (3, 1007, '2024-09-01', '2025-06-30');
INSERT INTO public.contracts (id, number, start_date, end_date) VALUES (4, 1012, '2024-09-01', '2025-06-30');
INSERT INTO public.contracts (id, number, start_date, end_date) VALUES (5, 1016, '2024-09-01', '2025-06-30');
INSERT INTO public.contracts (id, number, start_date, end_date) VALUES (6, 1020, '2024-09-01', '2025-06-30');
INSERT INTO public.contracts (id, number, start_date, end_date) VALUES (7, 1019, '2024-09-01', '2025-06-30');
INSERT INTO public.contracts (id, number, start_date, end_date) VALUES (8, 1018, '2024-09-01', '2025-06-30');
INSERT INTO public.contracts (id, number, start_date, end_date) VALUES (9, 1017, '2024-09-01', '2025-06-30');
INSERT INTO public.contracts (id, number, start_date, end_date) VALUES (10, 1015, '2024-09-01', '2025-06-30');
INSERT INTO public.contracts (id, number, start_date, end_date) VALUES (11, 1014, '2024-09-01', '2025-06-30');
INSERT INTO public.contracts (id, number, start_date, end_date) VALUES (12, 1013, '2024-09-01', '2025-06-30');
INSERT INTO public.contracts (id, number, start_date, end_date) VALUES (13, 1011, '2024-09-01', '2025-06-30');
INSERT INTO public.contracts (id, number, start_date, end_date) VALUES (14, 1010, '2024-09-01', '2025-06-30');
INSERT INTO public.contracts (id, number, start_date, end_date) VALUES (15, 1009, '2024-09-01', '2025-06-30');
INSERT INTO public.contracts (id, number, start_date, end_date) VALUES (16, 1008, '2024-09-01', '2025-06-30');
INSERT INTO public.contracts (id, number, start_date, end_date) VALUES (17, 1006, '2024-09-01', '2025-06-30');
INSERT INTO public.contracts (id, number, start_date, end_date) VALUES (18, 1005, '2024-09-01', '2025-06-30');
INSERT INTO public.contracts (id, number, start_date, end_date) VALUES (19, 1004, '2024-09-01', '2025-06-30');
INSERT INTO public.contracts (id, number, start_date, end_date) VALUES (20, 1002, '2024-09-01', '2025-06-30');

INSERT INTO public.dormitories (id, name, address) VALUES (1, 'Общежитие №1', 'ул. Студенческая, д. 1');

INSERT INTO public.rooms (id, number, total_places, dormitory_id) VALUES (1, 101, 4, 1);
INSERT INTO public.rooms (id, number, total_places, dormitory_id) VALUES (2, 102, 4, 1);
INSERT INTO public.rooms (id, number, total_places, dormitory_id) VALUES (3, 103, 4, 1);
INSERT INTO public.rooms (id, number, total_places, dormitory_id) VALUES (4, 104, 4, 1);
INSERT INTO public.rooms (id, number, total_places, dormitory_id) VALUES (5, 105, 4, 1);

INSERT INTO public.students (id, name, surname, patronymic, phone_number, age, chs, room_id, contract_id) VALUES (1, 'Иван', 'Иванов', 'Иванович', '+79001111111', 19, 85, 1, 1);
INSERT INTO public.students (id, name, surname, patronymic, phone_number, age, chs, room_id, contract_id) VALUES (2, 'Сергей', 'Сергеев', 'Сергеевич', '+79003333333', 21, 75, 1, 2);
INSERT INTO public.students (id, name, surname, patronymic, phone_number, age, chs, room_id, contract_id) VALUES (3, 'Николай', 'Николаев', 'Николаевич', '+79007777777', 21, 83, 2, 3);
INSERT INTO public.students (id, name, surname, patronymic, phone_number, age, chs, room_id, contract_id) VALUES (4, 'Роман', 'Романов', 'Романович', '+79012121212', 21, 76, 3, 4);
INSERT INTO public.students (id, name, surname, patronymic, phone_number, age, chs, room_id, contract_id) VALUES (5, 'Евгений', 'Евгеньев', 'Евгеньевич', '+79016161616', 21, 77, 4, 5);
INSERT INTO public.students (id, name, surname, patronymic, phone_number, age, chs, room_id, contract_id) VALUES (6, 'Олег', 'Олегов', 'Олегович', '+79020202020', 21, 81, 5, 6);
INSERT INTO public.students (id, name, surname, patronymic, phone_number, age, chs, room_id, contract_id) VALUES (7, 'Илья', 'Ильин', 'Ильич', '+79019191919', 23, 72, 5, 7);
INSERT INTO public.students (id, name, surname, patronymic, phone_number, age, chs, room_id, contract_id) VALUES (8, 'Максим', 'Максимов', 'Максимович', '+79018181818', 19, 94, 5, 8);
INSERT INTO public.students (id, name, surname, patronymic, phone_number, age, chs, room_id, contract_id) VALUES (9, 'Тимур', 'Тимуров', 'Тимурович', '+79017171717', 20, 86, 5, 9);
INSERT INTO public.students (id, name, surname, patronymic, phone_number, age, chs, room_id, contract_id) VALUES (10, 'Григорий', 'Григорьев', 'Григорьевич', '+79015151515', 22, 93, 4, 10);
INSERT INTO public.students (id, name, surname, patronymic, phone_number, age, chs, room_id, contract_id) VALUES (11, 'Кирилл', 'Кириллов', 'Кириллович', '+79014141414', 20, 84, 4, 11);
INSERT INTO public.students (id, name, surname, patronymic, phone_number, age, chs, room_id, contract_id) VALUES (12, 'Артём', 'Артёмов', 'Артёмович', '+79013131313', 19, 89, 4, 12);
INSERT INTO public.students (id, name, surname, patronymic, phone_number, age, chs, room_id, contract_id) VALUES (13, 'Денис', 'Денисов', 'Денисович', '+79011111111', 22, 91, 3, 13);
INSERT INTO public.students (id, name, surname, patronymic, phone_number, age, chs, room_id, contract_id) VALUES (14, 'Александр', 'Александров', 'Александрович', '+79010101010', 20, 87, 3, 14);
INSERT INTO public.students (id, name, surname, patronymic, phone_number, age, chs, room_id, contract_id) VALUES (15, 'Владимир', 'Владимиров', 'Владимирович', '+79009999999', 23, 70, 3, 15);
INSERT INTO public.students (id, name, surname, patronymic, phone_number, age, chs, room_id, contract_id) VALUES (16, 'Михаил', 'Михайлов', 'Михайлович', '+79008888888', 19, 95, 2, 16);
INSERT INTO public.students (id, name, surname, patronymic, phone_number, age, chs, room_id, contract_id) VALUES (17, 'Андрей', 'Андреев', 'Андреевич', '+79006666666', 20, 78, 2, 17);
INSERT INTO public.students (id, name, surname, patronymic, phone_number, age, chs, room_id, contract_id) VALUES (18, 'Дмитрий', 'Дмитриев', 'Дмитриевич', '+79005555555', 22, 92, 2, 18);
INSERT INTO public.students (id, name, surname, patronymic, phone_number, age, chs, room_id, contract_id) VALUES (19, 'Алексей', 'Алексеев', 'Алексеевич', '+79004444444', 19, 88, 1, 19);
INSERT INTO public.students (id, name, surname, patronymic, phone_number, age, chs, room_id, contract_id) VALUES (20, 'Пётр', 'Петров', 'Петрович', '+79002222222', 20, 90, 1, 20);

INSERT INTO public.violations (id, date, violation_type) VALUES (1, '2024-10-05', 'SMOKING');
INSERT INTO public.violations (id, date, violation_type) VALUES (2, '2024-11-12', 'DRINKING');
INSERT INTO public.violations (id, date, violation_type) VALUES (3, '2024-12-01', 'NOISE');
INSERT INTO public.violations (id, date, violation_type) VALUES (4, '2025-01-15', 'MESS');
INSERT INTO public.violations (id, date, violation_type) VALUES (5, '2025-02-20', 'DAMAGE');

INSERT INTO public.student_violations (student_id, violation_id) VALUES (1, 1);
INSERT INTO public.student_violations (student_id, violation_id) VALUES (2, 2);
INSERT INTO public.student_violations (student_id, violation_id) VALUES (3, 3);
INSERT INTO public.student_violations (student_id, violation_id) VALUES (4, 4);
INSERT INTO public.student_violations (student_id, violation_id) VALUES (5, 5);
INSERT INTO public.student_violations (student_id, violation_id) VALUES (1, 3);

SELECT pg_catalog.setval('public.contracts_id_seq', 20, true);
SELECT pg_catalog.setval('public.dormitories_id_seq', 1, true);
SELECT pg_catalog.setval('public.rooms_id_seq', 5, true);
SELECT pg_catalog.setval('public.students_id_seq', 20, true);
SELECT pg_catalog.setval('public.violations_id_seq', 5, true);