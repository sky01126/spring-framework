<%@ include file="/WEB-INF/jsp/includes.jsp" %>
<%@ include file="/WEB-INF/jsp/header.jsp" %>

<h2>Owner Information</h2>

  <table>
    <tr>
      <th>Name</th>
      <td><b>${owner.firstName} ${owner.lastName}</b></td>
    </tr>
    <tr>
      <th>Address</th>
      <td>${owner.address}</td>
    </tr>
    <tr>
      <th>City</th>
      <td>${owner.city}</td>
    </tr>
    <tr>
      <th>Telephone </th>
      <td>${owner.telephone}</td>
    </tr>
  </table>
  <table class="table-buttons">
    <tr>
      <td colspan="2" align="center">
        <a href="<spring:url value="${owner.id}/edit"/>">Edit Owner</a>
      </td>
      <td>
        <a href="<spring:url value="${owner.id}/pets/new"/>">Add New Pet</a>
      </td>
    </tr>
  </table>

  <h2>Pets and Visits</h2>

  <c:forEach var="pet" items="${owner.pets}">
    <table width="94%">
      <tr>
        <td valign="top">
          <table>
            <tr>
              <th>Name</th>
              <td><b>${pet.name}</b></td>
            </tr>
            <tr>
              <th>Birth Date</th>
              <td><fmt:formatDate value="${pet.birthDate}" pattern="yyyy-MM-dd"/></td>
            </tr>
            <tr>
              <th>Type</th>
              <td>${pet.type.name}</td>
            </tr>
          </table>
        </td>
        <td valign="top">
          <table>
            <tr>
            <thead>
              <th>Visit Date</th>
              <th>Description</th>
            </thead>
            </tr>
            <c:forEach var="visit" items="${pet.visits}">
              <tr>
                <td><fmt:formatDate value="${visit.date}" pattern="yyyy-MM-dd"/></td>
                <td>${visit.description}</td>
              </tr>
            </c:forEach>
          </table>
        </td>
      </tr>
    </table>
    <table class="table-buttons">
      <tr>
        <td>
          <spring:url value="${owner.id}/pets/{petId}" var="petUrl">
            <spring:param name="petId" value="${pet.id}"/>
          </spring:url>
          <a href="${petUrl}">Edit Pet</a>
        </td>
        <td></td>
        <td>
          <spring:url value="${owner.id}/pets/{petId}/visits/new" var="visitUrl">
            <spring:param name="petId" value="${pet.id}"/>
          </spring:url>
          <a href="${visitUrl}">Add Visit</a>
        </td>
      </tr>
    </table>
  </c:forEach>
  
<%@ include file="/WEB-INF/jsp/footer.jsp" %>
