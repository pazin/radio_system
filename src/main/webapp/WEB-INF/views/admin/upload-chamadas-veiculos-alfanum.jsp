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
            <div class="col-lg-6 col-md-6">
              <h3>Upload de Chamadas de Ve�culos<br/>Letras e N�meros </h3>
            </div>

            <div class="col-lg-6 col-md-6" id="alertArea">
            </div>
          </div>

          <div class="spacer-vertical20"></div>
          
          <div class="row">
            <div class="col-lg-12 col-md-12">

              <div class="row"> 

                <div class="col-lg-9 col-md-9">
                  <div class="panel panel-default">
                    <div class="panel-body">
                    
                      <form action="#" id="form-upload-alfanum" class="form">

                        <div class="row">
                          <div class="col-lg-12 ">
                            <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" id="csrf" />
                            <input type="hidden" name="codigo" value="veic_placa_letra" id="codigo" />  <!--  isso � apenas um default... o business vai determinar -->

                            <input type="file" id="fileupload" name="file" multiple style="display : none;">


                            <div class="col-lg-3 col-md-4 col-sm-6">
                              <span class="btn btn-primary btn-file">
                                  Escolha o arquivo<input type="file" id="outrofileupload" name="file2" >
                              </span>
                            </div>
                            
                            <div class="col-lg-offset-3 col-md-offset-4 col-sm-offset-6">          
                              <p class="form-control-static" id="static-arquivos"></p>
                            </div>

                            <div class="spacer-vertical10"></div>

                            <div id="resultados">
                              <div id="progress" class="progress">
                                  <div class="progress-bar progress-bar-success"></div>
                              </div>
                              <div id="files" class="files"></div>            
                            </div>
                            
                            <div class="form-group">
                              <label for="nome" class="control-label col-sm-4 col-md-4 col-xs-4">Letra ou N�mero:</label>
                              <div class="col-sm-2 col-md-2 col-lg-2 col-xs-3">
                                <input type="text" class="form-control" id="alfanumerico" name="alfanumerico" maxlength="2">
                              </div>
                            </div>           
                            
                          </div>
                        </div>
                        
                        <div class="row">
                          <div class="col-lg-6 col-md-6 col-sm-6">          
                          </div>
                          <div class="col-lg-6 col-md-6 col-sm-6">
                            <div class="pull-right-not-xs">
                              <a class="btn btn-success" id="btnIniciar" href="#"> <i class="fa fa-lg fa-cloud-upload"></i> Iniciar Upload</a>    
                            </div>          
                          </div>
                        </div>            

                      </form>

                    </div>
                  </div>
                </div>

              </div>

              <div class="spacer-vertical20"></div>
            
              <div class="row">
                <div class="form-group">
                  <label for="login" class="control-label col-lg-3 col-sm-4 col-md-4">Filtro do Componente da Chamada:</label>
                  <div class="col-lg-6 col-md-5 col-sm-8">
                    <select class="form-control" id="categoria-combo" name="categoria">
                      <option value="" selected="selected">Todos</option>
                      <option value="veic_placa_letra">Letras</option>
                      <option value="veic_placa_numero">N�meros</option>
                    </select>
                  </div>
                </div> 
              </div>

              <div class="spacer-vertical20"></div>
              
              <table  
                 id="table-chamadas-veiculos"
                 data-toggle="table"
                 data-url="${context}/admin/midias"
                 data-height="400"
                 data-side-pagination="server"
                 data-pagination="true"
                 data-page-size=6
                 data-locale = "pt_BR"
                 data-page-list="[6,12,25]"
                 data-unique-id="idMidia"
                 data-query-params="queryParams" >
                <thead>
                  <tr>
                      <th data-field="nome" class="col-lg-7 col-md-6">Nome do Arquivo</th>
                      <th data-field="descricao" class="col-lg-2 col-md-3">Letra ou N�mero</th>
                      <th data-field="idMidia" data-formatter="editarFormatter" class="col-lg-1 col-md-1 col-sm-1 col-xs-2">Editar</th>
                      <th data-field="idMidia" data-formatter="removerFormatter" class="col-lg-1 col-md-1 col-sm-1 col-xs-2">Remover</th>
                      <th data-field="idMidia" data-formatter="playFormatter" class="col-lg-1 col-md-1 col-sm-1 col-xs-2">Tocar</th>
                  </tr>
                </thead>
              </table>
            
            </div>
          </div>
          
          <div class="spacer-vertical10"></div>

          <div class="player" id="player1" style="display:none;" >
              <audio controls>
                  <source src="" type="audio/ogg">
              </audio>
          </div>
          
          
          <div class="spacer-vertical40"></div>
          
          <div class="row">
            <div class="col-lg-6 col-md-6 col-sm-6">          
                <a class="btn btn-default" href="${context}/admin/upload-painel/view">
                <i class="fa fa-arrow-left"></i>
                Voltar para Upload de M�dias</a>    
            </div>
            <div class="col-lg-6 col-md-6 col-sm-6">
              <div class="pull-right-not-xs">
                <a class="btn btn-default" href="${context}/admin/painel">Painel de Admin</a>    
              </div>          
            </div>
          </div>            
        </div>
      </div>
    </div>
    
      
  </div> <!-- /container -->


<div class="modal fade" id="myModal">
  <div class="modal-dialog modal-lg">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="titulo-modal">Alterar nome da Chamada de Ve�culo</h4>
      </div>
      <div class="modal-body">
        <form action="#" class="form-horizontal" id="altera-nome-midia-form" method="POST">
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
          <input type="hidden" id="idMidia" name="idMidia" value="0">
          
          <div class="row">
            <div class="col-lg-12 col-md-12">
              
              <div class="form-group">
                <label for="login" class="control-label col-sm-2 col-md-2">Nome</label>
                <div class="col-lg-8 col-md-10">
                  <input type="text" class="form-control" id="nomeMidia" name="nome">
                </div>
              </div>
              
              <div class="form-group">
                <label for="login" class="control-label col-sm-2 col-md-2">Letra ou N�mero</label>
                <div class="col-lg-8 col-md-10">
                  <input type="text" class="form-control" id="descricao" name="descricao" maxlength="2">
                </div>
              </div>
              
            </div> 
          </div>
          
        </form>
        
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Fechar</button>
        <button type="button" class="btn btn-primary" id="btnSalvar">Alterar</button>
      </div>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->



<div class="modal fade" id="myDialog">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close"><span aria-hidden="true">&times;</span></button>
        <h4 class="modal-title" id="titulo-modal">Remover Chamada de Ve�culo</h4>
      </div>
      <div class="modal-body">
        <form action="#" class="form-horizontal" id="remove-midia-form" method="POST">
          <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}" />
          <input type="hidden" id="idMidiaDialog" name="idMidia" value="0">
          
          <div class="row">
            <div class="col-lg-12 col-md-12">
              Deseja realmente remover essa Chamada de Ve�culo?
            </div> 
          </div>
          
        </form>
        
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" id="btnNaoDialog" data-dismiss="modal">N�o</button>
        <button type="button" class="btn btn-primary" id="btnConfirmarDelete">Sim</button>
      </div>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-table/1.11.0/bootstrap-table.min.js"></script>
<script type="text/javascript" src="https://cdnjs.cloudflare.com/ajax/libs/jquery.serializeJSON/2.7.2/jquery.serializejson.min.js" defer></script>

<script src="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-table/1.11.0/locale/bootstrap-table-pt-BR.min.js" charset="UTF-8"></script>
<link href="https://cdnjs.cloudflare.com/ajax/libs/bootstrap-table/1.11.0/bootstrap-table.min.css" rel="stylesheet">

<script src="https://cdnjs.cloudflare.com/ajax/libs/jqueryui/1.11.4/jquery-ui.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/blueimp-file-upload/9.12.5/js/jquery.iframe-transport.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/blueimp-file-upload/9.12.5/js/jquery.fileupload.min.js"></script>

<link rel="stylesheet" href="https://cdn.plyr.io/1.3.7/plyr.css" defer>
<script src="https://cdn.plyr.io/1.3.7/plyr.js" defer></script>

<script type="text/javascript">

    var pagina = 0, limit = 6;

    function queryParams(params) {

        params.pageNumber = $('#table-chamadas-veiculos').bootstrapTable('getOptions').pageNumber;
        params.codigo = $("#categoria-combo").val();
        return params;
    };

    function editarFormatter(value, row) {
        return '<a class="btn btn-link editar-class" id="btnEditarChamada" idMidia="'+ row.idMidia +'" href="#"> <i class="fa fa-lg fa-font"></i><i class="fa fa-lg fa-pencil"></i></a>';
    }
    
    function removerFormatter(value, row) {
        return '<a class="btn btn-link remover-class" id="btnRemoverChamada" idMidia="'+ row.idMidia +'" href="#"> <i class="fa fa-lg fa-times"></i></a>';
    }

    function playFormatter(value, row) {
        return '<a class="btn btn-link play-class" id="btnPlayChamada" idMidia="'+ row.idMidia +'" href="#"> <i class="fa fa-lg fa-play-circle"></i></a>';
    }
    
    var player = null;
    
    var playChamada = function( element )
    {
        player.pause();
        
        var idMidia = element.attr("idMidia");
        
        var url = buildUrl( "/admin/midias/{idMidia}", { idMidia: idMidia });
        
//         var source = { src: url, type : "audio/mp3" };
        
        player.source( url );
        player.play();
    }


    var openDialog = function( element )
    {
        var idMidia = element.attr("idMidia");
        
        $('#idMidiaDialog').val( idMidia );
        
        $('#myDialog').modal('show');
    }

    
    var deletar = function()
    {
        var idMidia = $("#idMidiaDialog").val();
        
        if ( idMidia == null || idMidia == 0 )
            preencheAlertGeral( "alertArea", "M�dia n�o encontrada" );

        var url = buildUrl( "/admin/chamada-veiculos/{idMidia}", { idMidia : idMidia } );
        
        $.ajax({
            type: 'DELETE',
            contentType: 'application/json',
            url: url,
            dataType: 'json'
        }).done( function(json){ 

            if (json.ok == 1){
                preencheAlertGeral( "alertArea", "Registro removido com sucesso", "success" );
                $("#table-chamadas-veiculos").bootstrapTable('refresh');
                $('#myDialog').modal('toggle');
            }
            else{
                $('#myDialog').modal('toggle');
                preencheErros( json.errors );
            }
        });
    } 
    

    
    
    var openPopup = function( element )
    {
        var idMidia = element.attr("idMidia");
        
        var row = $('#table-chamadas-veiculos').bootstrapTable('getRowByUniqueId', idMidia);
        
        $('#idMidia').val( idMidia );
        $('#nomeMidia').val( row.nome );
        $('#descricao').val( row.descricao );
        
        var texto = $("#categoria-combo :selected").text();
        
        $('#tipo').html( texto );
        
        $('#myModal').modal('show');
        $('#nomeMidia').focus();
    }


    var salvar = function()
    {
        var url = buildUrl( "/admin/chamada-veiculos");
        
        $.ajax({
            type: 'POST',
            contentType: 'application/json',
            url: url,
            dataType: 'json',
            data:  JSON.stringify( $('#altera-nome-midia-form').serializeJSON() )
            
        }).done( function(json){ 

            if (json.ok == 1){
                preencheAlertGeral( "alertArea", "Registro salvo com sucesso.", "success" );
                $("#table-chamadas-veiculos").bootstrapTable('refresh');
                $('#myModal').modal('toggle');
            }
            else{
                $('#myModal').modal('toggle');
                preencheErros( json.errors );
            }
        });
    } 
    
    
    
    var configuraUploader = function() 
    {
        var _url = buildUrl( "/admin/upload-chamadas-veiculos" );
        
        $('#fileupload').fileupload({
            dataType: 'json',
            url : _url,
            formData: { 
                _csrf: $("#csrf").val() 
            },
            add: function (e, data) {
                
                removeErros();
                
                var alfa = $("#alfanumerico").val();
                if ( alfa == null || alfa == "" || alfa.length > 2 )
                {
                    preencheAlertGeral("alertArea", "Preencha a Letra ou N�mero correspondente para realizar o upload.", "danger");
                    preencheErroField("alfanumerico", "Necess�rio");
                    return false;
                }
                
                data.formData = { 
                    _csrf: $("#csrf").val(), 
                    codigo : $("#codigo").val(),
                    descricao : $("#alfanumerico").val()
                };
                
                data.submit();
            },
            stop: function (e, data) {
                var erros = $("#alertArea .alert-danger").length;
                if ( erros == null || erros == 0 )
                    preencheAlertGeral( "alertArea", "Upload realizado com sucesso", "success" );
                $("#table-chamadas-veiculos").bootstrapTable('refresh');
                $('#progress .progress-bar').css(
                        'width',
                        0 + '%'
                    );
            },
            fail: function (e, data) {
                var errors = data.jqXHR.responseJSON.errors;
                preencheErros( errors );
                $('#progress .progress-bar').css(
                        'width',
                        0 + '%'
                    );
            },
            progressall: function (e, data) {
                var progress = parseInt(data.loaded / data.total * 100, 10);
                $('#progress .progress-bar').css(
                    'width',
                    progress + '%'
                );
            } 
        }); 

    }
   

    var iniciarUpload = function()
    {
        var filesList = $('#outrofileupload')[0].files;

        if ( filesList == null || filesList.length == 0 ) { 
            preencheAlertGeral( "alertArea", "Selecione o arquivo e adicione uma letra ou n�mero");
            return;
        }
        
        var alfanumerico = $("#alfanumerico").val()
        
        if ( alfanumerico == null || alfanumerico === "" ){
            preencheAlertGeral( "alertArea", "Escreva qual a Letra ou N�mero o arquivo representa.");
            return;
        }

        $('#fileupload').fileupload('add', { files : filesList } );
        
    }
    
    var mostrarArquivos = function()
    {
        var filesList = $('#outrofileupload')[0].files; 
       
        if ( filesList && filesList.length > 0 )
          $("#static-arquivos").html( filesList.length + " arquivo(s) selecionado(s)" );
        
    }
    
    $(function(){
        
        var token = $("input[name='_csrf']").val();
        var header = "X-CSRF-TOKEN";
        $(document).ajaxSend(function(e, xhr, options) {
            xhr.setRequestHeader(header, token);
        });

// PRECISO EVOLUIR O PLYR PARA A VERS�O 1.5 N�O ESTAVA TOCANDO....

// N�O ESTAVA NEM CHEGANDO NO SPRING, TALVEZ ELE NO ESTEJA FAZENDO GET.

        plyr.setup( { options : ["current-time", "duration", "mute"]});

        player = $('#player1')[0].plyr;

//         player = plyr.setup( { controls : ["restart", "rewind", "play", "current-time", "duration", "mute" ], fullscreen : { enabled : false } } )[0];
        
        $("#categoria-combo").change( function() {

           $("#table-chamadas-veiculos").bootstrapTable('refresh');

           $('#progress .progress-bar').css(
                   'width',
                   0 + '%'
               );
        });
        
        configuraUploader();
        
        $("#table-chamadas-veiculos").on( 'load-success.bs.table', function( e, data ) {
            $(".editar-class").click( function(){
                openPopup($(this));
            });

            $(".remover-class").click( function(){
                openDialog($(this));
            });
            
            $(".play-class").click( function(){
                playChamada($(this));
            });
        });
        
        $("#table-chamadas-veiculos").on( 'page-change.bs.table', function ( e, number, size ){
            $(".editar-class").click( function(){
                openPopup($(this));
            });
            
            $(".remover-class").click( function(){
                openDialog($(this));
            });

            $(".play-class").click( function(){
                playChamada($(this));
            });
        });
        
        $("#btnSalvar").click( function(){
            salvar();
        });

        $("#btnConfirmarDelete").click( function(){
            deletar();
        });
       
        $('#myModal').on('shown.bs.modal', function () {
            $('#nomeMidia').focus();
        })

        $('#myDialog').on('shown.bs.modal', function () {
            $('#btnNaoDialog').focus();
        })
        
        $("#btnIniciar").click( function(){
            iniciarUpload();  
        });
        
        $("#outrofileupload").blur(function(){
            mostrarArquivos();
        });

        $("#outrofileupload").change(function(){
            $('#progress .progress-bar').css(
                'width',
                0 + '%'
            );

            $("#alfanumerico").val('');
        });
    });

</script>

<style type="text/css">

#table-generos tr{
  cursor: pointer;
}

</style>

<jsp:include page="/WEB-INF/views/bottom.jsp" />