alter table if exists collection
    add column creator_id bigint;

alter table if exists collection
    add column updater_id bigint;

alter table if exists collection
    add column parent_id bigint;

alter table if exists content_base
    add column creator_id bigint;

alter table if exists content_base
    add column updater_id bigint;

alter table if exists exhibition_languages
    rename column exhibition_group_id to exhibition_collection_id;

alter table if exists exhibiton
    rename column group_id to collection_id;

alter table if exists point_of_interest
    add column creator_id bigint;

alter table if exists point_of_interest
    add column updater_id bigint;

create table point_of_interest_content (
   point_of_interest_id bigint not null,
   content_id bigint not null
);

alter table if exists video_content
    add column content_id bigint not null default 0;

alter table if exists video_content_sources
    add column video_content_content_id bigint not null default 0;

alter table if exists visit_history
    add column visitor_id bigint;

alter table if exists collection
    drop constraint fkcrmp66fva3vixtnov93u5440w;

alter table if exists collection
    add constraint FK_HistoryBase_CreatedBy
        foreign key (creator_id)
            references creator;

alter table if exists collection
    drop constraint fkdc064neuyn6sunxfacbi5at4i;

alter table if exists collection
    add constraint FK_HistoryBase_UpdatedBy
        foreign key (updater_id)
            references creator;

alter table if exists collection
    add constraint FK_Collection_Parent
        foreign key (parent_id)
            references collection;

alter table if exists content_base
    drop constraint fkjqx8vosh0mrtgryjoj08ds1p2;

alter table if exists content_base
    add constraint FK_HistoryBase_CreatedBy
        foreign key (creator_id)
            references creator;

alter table if exists content_base
    drop constraint fkef8a3o5txo46dednif7c5p12c;

alter table if exists content_base
    add constraint FK_HistoryBase_UpdatedBy
        foreign key (updater_id)
            references creator;

alter table if exists exhibiton
    drop constraint fk56penodk9he830xmrefkmbpne;

alter table if exists exhibiton
    add constraint FK_Exhibition_Collection
        foreign key (collection_id)
            references collection;

alter table if exists point_of_interest
    drop constraint fk3frsykf6ciam2mqbfudxju5q6;

alter table if exists point_of_interest
    add constraint FK_HistoryBase_CreatedBy
        foreign key (creator_id)
            references creator;

alter table if exists point_of_interest
    drop constraint fkhdqnpvseant3hlkj2d1yc98n8;

alter table if exists point_of_interest
    add constraint FK_HistoryBase_UpdatedBy
        foreign key (updater_id)
            references creator;

alter table if exists point_of_interest_content
    add constraint FK_PointOfInterest_Content_2_Content
        foreign key (content_id)
            references content_base;

alter table if exists point_of_interest_content
    add constraint FK_PointOfInterest_Content_2_PointOfInterest
        foreign key (point_of_interest_id)
            references point_of_interest;

alter table if exists video_content
    drop constraint fk9oemcpw6o5531fy2too5m4bug;

alter table if exists video_content
    add constraint FK_VideoContent_MediaContent
        foreign key (content_id)
            references content_base;

alter table if exists visit_history
    add constraint FK_VisitHistory_Visitor
        foreign key (visitor_id)
            references visitor;