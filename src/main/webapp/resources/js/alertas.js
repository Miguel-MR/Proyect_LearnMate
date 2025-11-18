
Swal.fire({
    icon: "#{facesContext.maximumSeverity.ordinal eq 0 ? 'success' : 'error'}",
    title: "#{facesContext.maximumSeverity.ordinal eq 0 ? 'Éxito' : 'Error'}",
    text: "#{facesContext.messageList[0].summary}",
    confirmButtonColor: '#efb810'
}).then(function () {
    if (success) {
        closeEmailModal();
    }
});
;