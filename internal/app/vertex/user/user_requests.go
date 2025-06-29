package user

type SignUpRequest struct {
	Identifier  string `json:"identifier" binding:"required"`
	Password    string `json:"password" binding:"required"`
	DisplayName string `json:"display_name" binding:"required"`
}

type TokenRequest struct {
	Identifier        string `json:"identifier" binding:"required"`
	Password          string `json:"password" binding:"required"`
	TargetNodeAddress string `json:"target_node_address"`
}
