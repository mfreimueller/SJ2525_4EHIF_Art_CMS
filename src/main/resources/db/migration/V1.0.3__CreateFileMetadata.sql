create table if not exists file_metadata (
    id bigint not null,
    stored_filename varchar(255) not null,
    original_filename varchar(255) not null,
    content_type varchar(127),
    file_size bigint,
    uploaded_at timestamp,
    content_id bigint,
    primary key (id),
    constraint FK_FileMetadata_Content
        foreign key (content_id)
            references content_base
);
