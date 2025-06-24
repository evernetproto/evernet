package user

import "gorm.io/gorm"

type User struct {
	gorm.Model
	NodeIdentifier string `json:"node_identifier" gorm:"uniqueIndex:identifier_node_identifier_idx"`
	Identifier     string `json:"identifier" gorm:"uniqueIndex:identifier_node_identifier_idx"`
	DisplayName    string `json:"display_name"`
	Password       string `json:"-"`
	Creator        string `json:"creator"`
}
