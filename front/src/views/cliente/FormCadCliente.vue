<template>

     <div class="wrapper py-250" >
        <div class="section page-header header-filter">
            <div class="container">
                <div class="md-layout">
                    <div class="md-layout-item md-size-70 md-small-size-100 md-xsmall-size-100 mx-auto">
                        <login-card header-color="primary">
                            <h4 slot="title" class="card-title">Novo Usuário</h4>
                            <p slot="title" class="description-title">Cadastre-se com</p>
                            <md-button slot="buttons" href="javascript:void(0)" class="md-just-icon md-simple md-white">
                                <i class="fab fa-facebook-square"></i>
                            </md-button>
                            <md-button slot="buttons" href="javascript:void(0)" class="md-just-icon md-simple md-white">
                                <i class="fab fa-twitter"></i>
                            </md-button>
                            <md-button slot="buttons" href="javascript:void(0)" class="md-just-icon md-simple md-white">
                                <i class="fab fa-google-plus-g"></i>
                            </md-button>
                            <p slot="description" class="description text-center">Ou tradicionalmente</p>
                            
                            <keep-alive slot="inputs">
                                <cad-dados-basicos v-bind:dados="{dadosPessoais: this.cliente.dadosPessoais}" v-if="telaCad == 0"></cad-dados-basicos>
                                <cad-dados-endereco-entrega v-bind:dados="{enderecosEntrega: this.cliente.enderecosEntrega}" v-if="telaCad == 1"></cad-dados-endereco-entrega>
                                <cad-dados-endereco v-bind:dados="{enderecosCobranca: this.cliente.enderecosCobranca}" v-if="telaCad == 2"></cad-dados-endereco>
                                <cad-dados-cartao v-bind:dados="{cartoes: this.cliente.cartoes}" v-if="telaCad == 3"></cad-dados-cartao>
                            </keep-alive>
                                <!--md-button type="button" v-if="telaCad >= 1" @click="telaCad-=1" class="text-left btn btn-primary btn-link btn-wd btn-lg">Voltar</md-button>
                                <md-button type="button" @click="telaCad+=1" v-if="telaCad <= 2" class="float-right btn btn-primary btn-link btn-wd btn-lg">Próximo</md-button>
                                <md-button type="button" v-if="telaCad == 3" class="float-right btn btn-primary btn-link btn-wd btn-lg">Salvar</md-button-->
                            
                            <md-button @click="telaCad-=1" v-if="telaCad >= 1" slot="footer" class="float-left md-simple md-primary md-lg">
                                Voltar
                            </md-button>
                            <md-button @click="proximo()" v-if="telaCad <= 2" slot="footer" class="float-right md-simple md-primary md-lg">
                                Próximo
                            </md-button>
                            <md-button @click="proximo()" v-if="telaCad == 3" slot="footer" class="float-right md-simple md-primary md-lg">
                                Salvar
                            </md-button>
                        </login-card>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import CadDadosBasicos from './CadDadosBasicos'
import CadDadosEndereco from './CadDadosEndereco'
import CadDadosCartao from './CadDadosCartao'
import CadDadosEnderecoEntrega from './CadDadosEnderecoEntrega'
import { LoginCard } from "@/mk/components";
import axios from 'axios';
import { eventBus } from '../../main';
import  DadosPessoais  from "@/model/DadosPessoais.js"
import  EnderecoCobranca  from "@/model/EnderecoCobranca.js"
import  EnderecoEntrega  from "@/model/EnderecoEntrega.js"
import  Cartao  from "@/model/Cartao.js"
import swal from 'sweetalert';
import { blockUI } from "@/assets/block-ui/block-ui";

export default {
    created(){
        var dadosAtuais = this; 
        eventBus.$on('page', function(e){
            if(e == 1){
                dadosAtuais.telaCad = 1
            } else if(e == 2){
                dadosAtuais.telaCad = 2
            } else if( e == 3) {
                dadosAtuais.telaCad = 3
            } else if( e == 4 ){
                dadosAtuais.enviar()
            }
        });
    },

    components:{LoginCard, CadDadosBasicos, CadDadosEndereco, CadDadosCartao, CadDadosEnderecoEntrega},
    data: () => ({
        telaCad : 0,
        cliente :   {
            dadosPessoais : {
                id : "",
                nome : "",
                sobrenome : "",
                email : "",
                genero : "",
                cpf : "",
                dataNascimento : "",
                tipoTelefone : "",
                telefone : "",
                ddd : "",
                senha1 : "",
                senha2 : "",
                senha : ""
            },
            enderecosEntrega : {
                tipoResidencia : "",
                tipoLogradouro : "",
                pais : "",
                estado : "",
                cidade : "",
                logradouro : "",
                numero : "",
                bairro : "",
                cep : "",
                observacao : "",
                nomeComposto : "",
                favorito : false
            },
            enderecosCobranca : {
                tipoResidencia : "",
                tipoLogradouro : "",
                pais : "",
                estado : "",
                cidade : "",
                logradouro : "",
                numero : "",
                bairro : "",
                cep : "",
                observacao : "",
                favorito : false
            },
            cartoes : {
                bandeira : "",
                numero : "",
                nomeImpresso : "",
                codSeguranca : "",
                preferencial : false
            }
        }
    }),// ./dados
    beforeDestroy: function(){
        this.telaCad = 0;
        this.cliente = null;
    },
    methods:{
        enviar: function(){
             $.blockUI({
                message: '<i class="fa fa-circle-notch fa-spin fa-5x"></i>' ,
    			css: { 
    				border: 'none',
    				backgroundColor: 'transparent',
    				color: '#f6f6f6'
    			}
            });
            var dadosAtuais = this;
            axios.post(`http://localhost:8082/DarkBook/cliente?operacao=SALVAR`, 
            this.cliente, {
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded; charset=UTF-8',
                }
            }).then(function(e){
                $.unblockUI();
                swal({
                    title: e.data,
                    icon: "success"
                });
                dadosAtuais.$router.push({ name:"login" })
            }).catch(function(e){
                $.unblockUI();
                try{
                    swal({
                        title: e.response.data,
                        icon: "error"
                    });
                } catch (e) {
                    swal({
                        title: e,
                        icon: "error"
                    });
                }
            })
          },
          proximo(){
              switch(this.telaCad){
                  case 0:
                    eventBus.$emit('validarDadosBasicos', true);
                    break;
                case 1:
                    eventBus.$emit('validarDadosEndereco', true);
                    break;
                case 2:
                    eventBus.$emit('validarDadosEnderecoCobranca', true);
                    break;
                case 3:
                    eventBus.$emit('validarDadosCartao', true);
                    break;
              }
              
          }
    }

}
</script>


<style>
@media(min-width: 50px) {
  .float-right{
        margin-left: 20em!important;
    }
}

@media(min-width: 600px) {
  .float-right{
        margin-left: 25em!important;
    }
}

@media(min-width: 1000px) {
  .float-right{
        position: absolute;
        left: 5%;
    }
    .float-left{
        position: absolute;
        right: 5%;
    }
}

@media(min-width: 1200px) {
  .float-right{
        position: absolute;
        left: 10%;
    }
    .float-left{
        position: absolute;
        right: 10%;
    }
}

.py-250{
    margin-top: -300px;
    padding-bottom: 300px;
}

.mt-login{
    padding-top: 200px;
}

.description-title{
    color:white !important;
    margin-bottom: -1.5em !important;
}

.erros{
    margin-bottom: 10px !important;
}

</style>
