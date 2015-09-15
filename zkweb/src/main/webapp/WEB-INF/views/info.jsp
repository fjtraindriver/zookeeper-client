<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<jsp:include page="head.jsp"></jsp:include>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<style type="text/css">
td { font-size: 12px }
th {
     font-size: 12px;
}
body {
	margin-left: 10px;
}
</style>

<script type="text/javascript">
    function saveNodeData() {
        $.messager.progress();
        $.post("zk/saveData.html", {path: '${path}',cacheId: '${cacheId}',data: $("#dataValue").val()},
            function(data){
                $.messager.alert('提示', data);
                $.messager.progress('close');
            }
        );        
    }
</script>
</head>
<body>
    <form id="ff" method="post">
    <input type="hidden" value="${path}" name="path" />
    <input type="hidden" value="${cacheId}" name="cacheId" />
	<div>
		<table>
			<tr>
				<td><label >data：</label></td>
				<td>
				<textarea id="dataValue" rows="5" cols="100" name="data">${data}</textarea>
			</tr>
			<tr>
				<td><label >czxid：</label></td>
				<td>${czxid }</td>
			</tr>
			<tr>
				<td><label >mzxid：</label></td>
				<td>${mzxid }</td>
			</tr>
			<tr>
				<td><label >ctime：</label></td>
				<td>${ctime }</td>
			</tr>
			<tr>
				<td><label >mtime：</label></td>
				<td>${mtime }</td>
			</tr>
			<tr>
				<td><label >version：</label></td>
				<td>${version }</td>
			</tr>
			<tr>
				<td><label >cversion：</label></td>
				<td>${cversion }</td>
			</tr>
			<tr>
				<td><label >aversion：</label></td>
				<td>${aversion }</td>
			</tr>
			<tr>
				<td><label >ephemeralOwner：</label></td>
				<td>${ephemeralOwner }</td>
			</tr>
			<tr>
				<td><label >dataLength：</label></td>
				<td>${dataLength }</td>
			</tr>
			<tr>
				<td><label >numChildren：</label></td>
				<td>${numChildren }</td>
			</tr>
			<tr>
				<td><label >pzxid：</label></td>
				<td>${pzxid }</td>
			</tr>
				<c:forEach items="${acls }" var="acl">
					<tr>
						<td><label >scheme</label></td>
						<td>${acl.scheme }</td>
					</tr>
					<tr>
						<td><label >id</label></td>
						<td>${acl.id }</td>
					</tr>
					<tr>
						<td><label >perms</label></td>
						<td>${acl.perms }</td>
					</tr>
				</c:forEach>
		</table>
	</div>
	<br/>
	</form>
	<a href="#" class="easyui-linkbutton" data-options="iconCls:'icon-save'" onclick="saveNodeData();">保存</a>
</body>
</html>