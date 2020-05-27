delete from messages;

insert into messages(id, text, tag, user_id) values
(5, 'first text', 'first tag', 1),
(6, 'second text', 'second tag', 1),
(7, 'third text', 'third tag', 1);

alter sequence hibernate_sequence restart with 10;
