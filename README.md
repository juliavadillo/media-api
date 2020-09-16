# mediaApi
REST API developed to manage medias

Esssa API tem como objetivo receber registros de medias e publicados no S3 Bucker da AWS.
Aplicação disponibilizada na AWS: http://mediaapp.sa-east-1.elasticbeanstalk.com/medias?deleted=false
Documentação Open API realizada com o Swagger disponível no link: http://mediaapp.sa-east-1.elasticbeanstalk.com/swagger-ui/index.html#/media-resource

Rodar a aplicação localmente: 
1. Necessário criar um banco de dados Postgres.
  - Nome do banco: media-apirest
  - usuario: postgres
  - senha: postgres
  ** Os valores de banco de dados podem ser alterados acessando o arquivo de configuração application.properties e setando o valor desejado

2. Via IDE(Eclipse, etc): Selecionar a classe ApiApplication.java -> Run As -> Java Application
   ou
   Via console: -Na pasta api(media-api/api) abrir o console e rodar o comando "mvn clean install"
                -Após a finalização da build será criado um arquivo .jar na pasta target (media-api/api/target)
                -Acessar a pasta target via console e executar o comando java -jar "nome-do-jar-gerado.jar" (ex: java -jar api.0.0.1-SNAPSHOT.jar)

Após esses passos a aplicação estará disponível no endereço //localhost:8080 e os recursos disponíveis poderão ser acessados através do link do swagger http://localhost:8080/swagger-ui/index.html#/


Rodar testes unitários localmente:
Via IDE(Eclipse, etc): Selecionar a classe MediaResourceTest.java -> Run As -> JUnitTest
   ou
   Via console: -Na pasta api(media-api/api) abrir o console e rodar o comando "mvn test"
   
 
Coleção POSTMAN de request: https://www.getpostman.com/collections/3fac633ab3e23fa1bbdb
