delete from user_role;
delete from users;

insert into users(id, username, password, active) values
(1, 't', '$2a$08$UPZ.UX.LctkL6TeNckNWQuipkxAj1n.mofQ.JyrDCRQjk6qyYmY3i', true),
(2, 'a', '$2a$08$xFmun8uDaHNd/pjL6YuXcepOATYVAd.t3F.RgNFlxHrbq99eA6NGq', true);

insert into user_role(user_id, roles) values
(1, 'USER'), (1, 'ADMIN'),
(2, 'USER');
