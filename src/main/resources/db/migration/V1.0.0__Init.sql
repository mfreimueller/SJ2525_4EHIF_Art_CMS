create sequence global_seq start with 1 increment by 1;
create table audio_content
(
    id bigint not null,
    primary key (id)
);
create table audio_content_transcriptions
(
    link_type        char(1),
    audio_content_id bigint       not null,
    code             varchar(255) not null,
    file_path        varchar(255),
    name             varchar(255) not null,
    primary key (audio_content_id, code, name)
);
create table collection
(
    created_at    timestamp(6) with time zone,
    created_by_id bigint,
    id            bigint not null,
    updated_at    timestamp(6) with time zone,
    updated_by_id bigint,
    version       bigint,
    primary key (id)
);
create table collection_point_of_interest
(
    collection_id         bigint not null,
    points_of_interest_id bigint not null,
    primary key (collection_id, points_of_interest_id)
);
create table collection_sub_collections
(
    collection_id      bigint not null,
    sub_collections_id bigint not null unique,
    primary key (collection_id, sub_collections_id)
);
create table collection_titles
(
    collection_id bigint       not null,
    code          varchar(255) not null,
    name          varchar(255) not null,
    title         varchar(255),
    primary key (collection_id, code, name)
);
create table content_localized_descriptions
(
    content_id  bigint       not null,
    code        varchar(255) not null,
    description varchar(255),
    name        varchar(255) not null,
    primary key (content_id, code, name)
);
create table content_base
(
    created_at    timestamp(6) with time zone,
    created_by_id bigint,
    id            bigint not null,
    updated_at    timestamp(6) with time zone,
    updated_by_id bigint,
    version       bigint,
    primary key (id)
);
create table creator
(
    role     char(1),
    id       bigint      not null,
    version  bigint,
    username varchar(64) not null,
    primary key (id)
);
create table exhibition_languages
(
    exhibition_group_id bigint not null,
    code                varchar(255),
    name                varchar(255)
);
create table exhibiton
(
    group_id bigint not null,
    primary key (group_id)
);
create table image_content
(
    link_type  char(1),
    content_id bigint not null,
    file_path  varchar(255),
    primary key (content_id)
);
create table point_of_interest
(
    created_at    timestamp(6) with time zone,
    created_by_id bigint,
    id            bigint not null,
    updated_at    timestamp(6) with time zone,
    updated_by_id bigint,
    version       bigint,
    primary key (id)
);
create table point_of_interest_descriptions
(
    point_of_interest_id bigint       not null,
    code                 varchar(255) not null,
    description          varchar(255),
    name                 varchar(255) not null,
    primary key (point_of_interest_id, code, name)
);
create table point_of_interest_titles
(
    point_of_interest_id bigint       not null,
    code                 varchar(255) not null,
    name                 varchar(255) not null,
    title                varchar(255),
    primary key (point_of_interest_id, code, name)
);
create table slideshow_content
(
    duration   integer,
    mode       char(1),
    content_id bigint not null,
    primary key (content_id)
);
create table slideshow_content_slides
(
    slides_id                    bigint not null unique,
    slideshow_content_content_id bigint not null
);
create table text_content
(
    content_id bigint not null,
    primary key (content_id)
);
create table text_content_long_texts
(
    text_content_content_id bigint       not null,
    code                    varchar(255) not null,
    long_text               varchar(255),
    name                    varchar(255) not null,
    primary key (text_content_content_id, code, name)
);
create table text_content_short_texts
(
    text_content_content_id bigint       not null,
    code                    varchar(255) not null,
    name                    varchar(255) not null,
    short_text              varchar(255),
    primary key (text_content_content_id, code, name)
);
create table video_content
(
    id bigint not null,
    primary key (id)
);
create table video_content_sources
(
    link_type        char(1),
    video_content_id bigint       not null,
    code             varchar(255) not null,
    file_path        varchar(255),
    name             varchar(255) not null,
    primary key (video_content_id, code, name)
);
create table visit_history
(
    duration   integer,
    id         bigint not null,
    version    bigint,
    visited_on timestamp(6) with time zone,
    primary key (id)
);
create table visit_history_point_of_interest
(
    points_of_interest_id bigint not null,
    visit_history_id      bigint not null
);
create table visitor
(
    id            bigint      not null,
    version       bigint,
    username      varchar(64) not null,
    email_address varchar(255),
    primary key (id)
);
create table visitor_visit_histories
(
    visit_histories_id bigint not null unique,
    visitor_id         bigint not null
);
alter table if exists audio_content add constraint FKix9txcmbtfnx03wdoi39j7x85 foreign key (id) references content_base;
alter table if exists audio_content_transcriptions add constraint FK_AudioContent_Transcriptions foreign key (audio_content_id) references audio_content;
alter table if exists collection add constraint FKcrmp66fva3vixtnov93u5440w foreign key (created_by_id) references creator;
alter table if exists collection add constraint FKdc064neuyn6sunxfacbi5at4i foreign key (updated_by_id) references creator;
alter table if exists collection_point_of_interest add constraint FK_Collection_PointOfInterest_2_Collection foreign key (points_of_interest_id) references point_of_interest;
alter table if exists collection_point_of_interest add constraint FK_Collection_PointOfInterest_2_PointOfInterest foreign key (collection_id) references collection;
alter table if exists collection_sub_collections add constraint FKkjfuvbyqr3lwirpt5ey9lc7s1 foreign key (sub_collections_id) references collection;
alter table if exists collection_sub_collections add constraint FK_Collection_SubCollections foreign key (collection_id) references collection;
alter table if exists collection_titles add constraint FK_Collection_Titles foreign key (collection_id) references collection;
alter table if exists content_localized_descriptions add constraint FK_Content_LocalizedDescriptions foreign key (content_id) references content_base;
alter table if exists content_base add constraint FKjqx8vosh0mrtgryjoj08ds1p2 foreign key (created_by_id) references creator;
alter table if exists content_base add constraint FKef8a3o5txo46dednif7c5p12c foreign key (updated_by_id) references creator;
alter table if exists exhibition_languages add constraint FK_Exhibition_Languages foreign key (exhibition_group_id) references exhibiton;
alter table if exists exhibiton add constraint FK56penodk9he830xmrefkmbpne foreign key (group_id) references collection;
alter table if exists image_content add constraint FK353uqh50v8rdf9up7dfi84tkw foreign key (content_id) references content_base;
alter table if exists point_of_interest add constraint FK3frsykf6ciam2mqbfudxju5q6 foreign key (created_by_id) references creator;
alter table if exists point_of_interest add constraint FKhdqnpvseant3hlkj2d1yc98n8 foreign key (updated_by_id) references creator;
alter table if exists point_of_interest_descriptions add constraint FK_PointOfInterest_Descriptions foreign key (point_of_interest_id) references point_of_interest;
alter table if exists point_of_interest_titles add constraint FK_PointOfInterest_Titles foreign key (point_of_interest_id) references point_of_interest;
alter table if exists slideshow_content add constraint FKewuvuxkr1qd19h0vmb0xij1cj foreign key (content_id) references content_base;
alter table if exists slideshow_content_slides add constraint FKowduvqnilsoi2r4juo4pm69d6 foreign key (slides_id) references content_base;
alter table if exists slideshow_content_slides add constraint FK_SlideshowContent_Slides foreign key (slideshow_content_content_id) references slideshow_content;
alter table if exists text_content add constraint FK7r53boms9yocxwbccrmg5lmyo foreign key (content_id) references content_base;
alter table if exists text_content_long_texts add constraint FK_TextContent_LongTexts foreign key (text_content_content_id) references text_content;
alter table if exists text_content_short_texts add constraint FK_TextContent_ShortTexts foreign key (text_content_content_id) references text_content;
alter table if exists video_content add constraint FK9oemcpw6o5531fy2too5m4bug foreign key (id) references content_base;
alter table if exists video_content_sources add constraint FK_VideoContent_Sources foreign key (video_content_id) references video_content;
alter table if exists visit_history_point_of_interest add constraint VisitHistory_PointOfInterest_2_VisitHistory foreign key (points_of_interest_id) references point_of_interest;
alter table if exists visit_history_point_of_interest add constraint FK_VisitHistory_PointOfInterest_2_PointOfInterest foreign key (visit_history_id) references visit_history;
alter table if exists visitor_visit_histories add constraint FKg50q43k3xc84tbuoann6saguj foreign key (visit_histories_id) references visit_history;
alter table if exists visitor_visit_histories add constraint FK_Visitor_VisitHistories foreign key (visitor_id) references visitor;
