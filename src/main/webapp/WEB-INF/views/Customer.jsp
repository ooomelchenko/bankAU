<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<html>

<script src="${pageContext.request.contextPath}/resources/js/jquery-3.2.1.js" type="text/javascript"></script>
<script src="${pageContext.request.contextPath}/resources/bootstrap/js/bootstrap.bundle.min.js"
        type="text/javascript"></script>

<link rel="stylesheet" media="screen" type="text/css"
      href="${pageContext.request.contextPath}/resources/bootstrap/css/bootstrap.css"/>
<link rel="stylesheet" media="screen" type="text/css"
      href="${pageContext.request.contextPath}/resources/css/general_style.css"/>

<head>
    <title>Customer</title>
</head>

<body>

<header>
    <div id="div_left_side" class="div_header_additions">
    </div>
    <div id="div_sheet_header">
        <h1>Картка клієнта</h1>
    </div>
    <div id="div_right_side" class="div_header_additions">
    </div>
</header>

<div class="container-fluid">

    <div class="row">

        <div class="jumbotron col-md-12">

            <table class="table table-hover">

                <tr>
                    <th>ІНН покупця</th>
                    <td>
                        <input type="number" min="0" value="<c:out value="${requestScope.customer.getCustomerInn()}"/>">
                    </td>
                </tr>
                <tr>
                    <th>Прізвище</th>
                    <td>
                        <input type="text" value="<c:out value="${requestScope.customer.lastName} "/>">
                    </td>
                </tr>
                <tr>
                    <th>Ім'я</th>
                    <td>
                        <input type="text" value="<c:out value="${requestScope.customer.customerName}"/>">
                    </td>
                </tr>
                <tr>
                    <th>По-батькові</th>
                    <td>
                        <input type="text" value="<c:out value="${requestScope.customer.middleName}"/>">
                    </td>
                </tr>
                <tr>
                    <th>Одружений(на)</th>
                    <td>
                        <input type="checkbox"
                               <c:if test="${requestScope.customer.isMerried()==true}">checked="checked"</c:if>>
                    </td>
                </tr>
                <tr>
                    <th>Підписант</th>
                    <td>
                        <select>
                            <c:forEach var="type" items="${requestScope.customer.getType().values()}">
                                <option value="${type.name()}" <c:if test="${type==requestScope.customer.getType().name()}">selected="selected"</c:if> >
                                        ${type.getUkrType()}
                                </option>
                            </c:forEach>
                        </select>
                    </td>
                </tr>

                <tr>
                    <th></th>
                    <td>
                        <button type="submit" class="btn-lg btn-success" style="/*display: none;*/ ">Прийняти</button>
                    </td>
                </tr>

            </table>


        </div>

    </div>

</div>

</body>
</html>
