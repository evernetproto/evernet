package admin

type TokenResponse struct {
	Token string `json:"token"`
}

type PasswordResponse struct {
	Password string `json:"password"`
	Admin    *Admin `json:"admin"`
}
