<jsp:include page="/WEB-INF/views/main.jsp" />    
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<c:set var="context" value="${pageContext.request.contextPath}" />
<meta name="_csrf" th:content="${_csrf.token}"/>

<jsp:include page="/WEB-INF/views/top.jsp" />    

  <div class="container">

    <div class="row">
    
      <div class="panel panel-default">
        <div class="panel-body">

          <div class="row">
            <div class="loader" id="ajaxload" style="display : none;"></div>
          </div>

          <div class="row">
            <div class="col-lg-6 col-md-6">
              <h3>Incluir Ambiente<br/>
                <small>Preencha as informações</small>
              </h3>
            </div>

            <div class="col-lg-6 col-md-6" id="alertArea">
            </div>
          </div>

          <div class="spacer-vertical40"></div>
          
          <form class="form-horizontal" id="ambiente-form" action="#" role="form">
            
            <!-- Necessário pro Spring -->
            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
           
            <div class="form-group">
              <label for="login" class="control-label col-sm-2 col-md-3">Nome do Ambiente:</label>
              <div class="col-sm-10 col-md-8">
                <input type="text" class="form-control" id="nome_amb" name="nome">
              </div>
            </div>
            
            <div class="form-group">
              <label for="login" class="control-label col-sm-2 col-md-3">Login:</label>
              <div class="col-sm-5 col-md-4">
                <input type="email" class="form-control" id="login_amb" placeholder="Login" name="login" style="text-transform: lowercase;" >
              </div>
            </div>
            
            <div class="form-group">
              <label for="login" class="control-label col-sm-2 col-md-3">Senha:</label>
              <div class="col-sm-5 col-md-4">
                <input type="password" class="form-control" id="password" placeholder="Senha" name="password">
              </div>
              
              <div class="checkbox col-lg-4 col-md-4 col-sm-4 col-xs-12">
                <label>
                  <input type="checkbox" id="mostrarSenha" name="mostrarSenha" value="false"> Mostrar senha
                </label>
              </div>
            </div>
            
            <div class="form-group">
              <label for="login" class="control-label col-sm-2 col-md-3"></label>
              <div class="col-sm-2 col-md-3">
                <button type="button" class="btn btn-primary" id="btnAdicionar">
                  <i class="fa fa-floppy-o"></i>
                  Adicionar
                </button>
              </div>
            </div>
            
          </form>
          
          <div class="spacer-vertical80"></div>
          
          <div class="row">
            <div class="col-lg-6 col-md-6 col-sm-6">
              <div class="">
                <a class="btn btn-default" href="${context}/ambientes/searches" >
                  <i class="fa fa-arrow-left"></i>
                  Voltar para Administrar Ambientes</a>
              </div>            
            </div>
            <div class="col-lg-6 col-md-6 col-sm-6">
              <div class="pull-right-not-xs">
                <a class="btn btn-default" href="${context}/principal">Painel Gerencial</a>
              </div>            
            </div>
          </div>
          
        </div>  <!-- panel body -->
      </div>
    </div>
    
      
  </div> <!-- /container -->
  
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery.serializeJSON/2.7.2/jquery.serializejson.min.js"></script>  
<script type="text/javascript" src="${context}/js/required/zxcvbn.js"></script>
  
<script type="text/javascript">


    var validaForm = function(){
        
        
        var isOk = true;
        
        removeErros();
        
        var arrayCampos = [
                            {field: "nome_amb",          desc : "Nome do Ambiente"},
                            {field: "login_amb",         desc : "Login" }, 
                            {field: "password",      desc : "Senha"}
                          ];
        
        isOk = validaCampos( arrayCampos );
        
        return isOk;
    };

    var salvar = function(){
        
        if ( validaForm() ){
            
            $("#btnAdicionar").prop("disabled",true);
            $('#ajaxload').show();

            $.ajax({
                type: 'POST',
                contentType: 'application/json',
                url: '${context}/ambientes',
                dataType: 'json',
                data: JSON.stringify( $('#ambiente-form').serializeJSON() )
            }).done( function(json){ 
                  
                if (json.ok != null ){
                   
                    preencheAlertGeral( "alertArea", "Registro salvo com sucesso.", "success" );
                      
                    jump(''); // topo da pagina
                }
                else{
                    preencheErros( json.errors );
                }

                $('#ajaxload').hide();
                $("#btnAdicionar").prop("disabled",false);
                
            }).fail( function(){
                $('#ajaxload').hide();
                $("#btnAdicionar").prop("disabled",false);
            });
        }
        
    };

    $(function(){
       
        var token = $("input[name='_csrf']").val();
        var header = "X-CSRF-TOKEN";
        $(document).ajaxSend(function(e, xhr, options) {
            xhr.setRequestHeader(header, token);
        });
        
        $('#btnAdicionar').on('click', salvar);
        
        $('#password').keyup( function( event ) {
            keyup_validasenha( $("ambiente-form"), event );
        });

        $("#mostrarSenha").click(function(){
            if ( $("#mostrarSenha").prop('checked') )
                $("#password").attr("type", "input");
            else
                $("#password").attr("type", "password");
        });
        
    });

</script>
  

<jsp:include page="/WEB-INF/views/bottom.jsp" />