<%
  if ( "true".equals( request.getParameter( "edit" ) ) )
  {
%>

<!-- Libs -->
<script type="text/javascript" charset="UTF-8" src="../../admin2/live-edit/lib/jquery-1.8.3.min.js"></script>
<script type="text/javascript" charset="UTF-8" src="../../admin2/live-edit/lib/jquery-ui-1.9.2.custom.min.js"></script>
<script type="text/javascript" charset="UTF-8" src="../../admin2/live-edit/lib/jquery.simulate.js"></script>
<script type="text/javascript" charset="UTF-8" src="../../admin2/live-edit/lib/jquery.ui.touch-punch.min.js"></script>
<script type="text/javascript" charset="UTF-8" src="../../admin2/live-edit/lib/jquery.ba-resize.min.js"></script>

<!-- It is important that this is loaded right after all jQuery dependencies -->
<script type="text/javascript" charset="UTF-8" src="../../admin2/live-edit/lib/jquery.noconflict.js"></script>
<script type="text/javascript" charset="UTF-8" src="../../admin2/live-edit/js/all.js"></script>

<%
  }
%>
