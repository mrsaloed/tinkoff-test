create table if not exists Requests
(
  id UUID primary key,
  inputData CLOB not null,
  outputData CLOB not null,
  requestTime time not null,
  parameters varchar not null,
  ipAddress varchar(39) not null
);

create table if not exists Words
(
    requestId UUID not null,
    word CLOB not null
);

alter table Words add foreign key (requestId) references Requests(id)