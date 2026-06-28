    package br.edu.atitus.model;

    import com.fasterxml.jackson.annotation.JsonProperty;
    import jakarta.persistence.*;
    import java.util.UUID;

    @Entity
    @Table(name = "users")
    public class UserEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.AUTO)
        private UUID id;

        @Column(nullable = false, unique = true, length = 50)
        private String username;

        @Column(nullable = false, unique = true, length = 100)
        private String email;

        @Column(nullable = false)
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) // Aceita a senha no POST, mas não devolve no JSON
        private String password;

        // Construtor Padrão (Obrigatório para o Hibernate)
        public UserEntity() {
        }

        // Construtor Completo (Opcional, ajuda em testes se necessário)
        public UserEntity(UUID id, String username, String email, String password) {
            this.id = id;
            this.username = username;
            this.email = email;
            this.password = password;
        }

        // Getters e Setters
        public UUID getId() {
            return id;
        }

        public void setId(UUID id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }