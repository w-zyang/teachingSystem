USE web_experiment;

-- 涓篒D=17鐨勫鐢熸坊鍔犻€夎璁板綍
INSERT INTO student_course (student_id, course_id, status, enroll_time) VALUES 
(17, 1, 'enrolled', '2024-02-20 09:00:00'),
(17, 6, 'enrolled', '2024-02-20 09:30:00'),
(17, 16, 'enrolled', '2024-02-20 10:00:00')
ON DUPLICATE KEY UPDATE status = 'enrolled';

-- 涓篒D=17鐨勫鐢熸坊鍔犲涔犺繘搴?INSERT INTO student_progress (student_id, course_id, status, progress, start_time, study_duration) VALUES 
(17, 1, 'in_progress', 80, '2024-03-01 09:00:00', 180),
(17, 6, 'in_progress', 60, '2024-03-05 14:00:00', 150),
(17, 16, 'in_progress', 50, '2024-03-10 16:00:00', 120)
ON DUPLICATE KEY UPDATE progress = VALUES(progress);
