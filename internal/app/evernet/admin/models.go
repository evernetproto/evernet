package admin

import "time"

type Admin struct {
	Identifier string    `json:"identifier"`
	Password   string    `json:"password,omitempty"`
	Creator    string    `json:"creator"`
	CreatedAt  time.Time `json:"created_at"`
	UpdatedAt  time.Time `json:"updated_at"`
}
