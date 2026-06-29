CREATE TABLE user_favorites (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    vehicle_id UUID NOT NULL,
    CONSTRAINT uk_user_vehicle UNIQUE (user_id, vehicle_id)
);