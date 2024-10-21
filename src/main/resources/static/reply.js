document.addEventListener('DOMContentLoaded', function() {
    const createButton = document.getElementById("create-btn");
    const bnoElement = document.getElementById("bno");
    const uidElement = document.getElementById("uid");

    if (createButton && bnoElement && uidElement) {
        createButton.addEventListener("click", event => {
            const replyElement = document.getElementById("newreply");
            if (replyElement) {
                postReply(bnoElement.value, uidElement.value, null, replyElement.value);
            } else {
                console.error('필요한 입력 요소를 찾을 수 없습니다.');
            }
        });

        const bno = bnoElement.value;
        if (bno) {
            getComments(bno);
        } else {
            console.error('게시글 번호를 찾을 수 없습니다.');
        }
    }

    checkLikeStatus();

    document.getElementById('heartbtn').addEventListener("click", function() {
        const bno = bnoElement.value;
        const uid = uidElement.value;

        $.ajax({
            type: "POST",
            url: "/like",
            data: JSON.stringify({ bno: bno, uid: uid }),
            contentType: "application/json",
            success: function(response) {
                if (response === 'liked') {
                    $('#heartbtn').addClass('liked');
                } else {
                    $('#heartbtn').removeClass('liked');
                }
            }
        });
    });
});

function postReply(bno, uid, parentRno, replyValue) {
    const replyElement = document.getElementById("newreply");
    fetch("/api/reply", {
        method: 'POST',
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            bno: bno,
            uid: uid,
            parentRno: parentRno,
            reply: replyValue,
        }),
    }).then(response => {
        if (!response.ok) {
            throw new Error('댓글 등록에 실패했습니다.');
        }
        return response.json();
    }).then(data => {
        alert("댓글 등록 완료");
        replyElement.value = '';
        getComments(bno);
    }).catch(error => {
        console.error('댓글 등록 중 오류 발생:', error);
        alert("댓글 등록에 실패했습니다. 오류 메시지: " + error.message);
    });
}

function postReReply(bno, uid, parentRno, replyValue) {
    fetch("/api/reply", {
        method: 'POST',
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            bno: bno,
            uid: uid,
            parentRno: parentRno,
            reply: replyValue,
        }),
    }).then(response => {
        if (!response.ok) {
            throw new Error('대댓글 등록에 실패했습니다.');
        }
        return response.json();
    }).then(data => {
        alert("대댓글 등록 완료");
        getComments(bno);
    }).catch(error => {
        console.error('대댓글 등록 중 오류 발생:', error);
        alert("대댓글 등록에 실패했습니다. 오류 메시지: " + error.message);
    });
}

function getComments(bno, page = 1, size = 3) {
    fetch(`/api/reply/${bno}?page=${page}&size=${size}`)
        .then(response => {
            if (!response.ok) {
                throw new Error('댓글을 가져오는 중 오류 발생: ' + response.statusText);
            }
            return response.json();
        })
        .then(data => {
            if (!data.comments || !Array.isArray(data.comments)) {
                throw new TypeError('댓글 데이터가 올바르지 않습니다.');
            }
            const commentList = document.getElementById('commentList');
            commentList.innerHTML = '<h2>댓글 목록</h2><hr>';
            const commentsMap = new Map();

            data.comments.forEach(comment => {
                const commentElement = createCommentElement(comment);
                commentsMap.set(comment.rno, commentElement);

                commentList.appendChild(commentElement);

                if (comment.childReplies && Array.isArray(comment.childReplies)) {
                    comment.childReplies.forEach(childReply => {
                        const childCommentElement = createCommentElement(childReply);
                        const repliesContainer = commentElement.querySelector('.reReplies');
                        repliesContainer.appendChild(childCommentElement);
                        commentsMap.set(childReply.rno, childCommentElement);
                    });
                }
            });

            attachEventListeners();

            // Add pagination
            const paginationContainer = document.getElementById('pagination');
            paginationContainer.innerHTML = '';

            if (data.totalPages > 1) {
                for (let i = 1; i <= data.totalPages; i++) {
                    const pageButton = document.createElement('button');
                    pageButton.innerText = i;
                    if (i === data.currentPage) {
                        pageButton.disabled = true;
                    }
                    pageButton.addEventListener('click', () => {
                        getComments(bno, i, size);
                    });
                    paginationContainer.appendChild(pageButton);
                }
            }
        })
        .catch(error => {
            console.error('댓글을 가져오는 중 오류 발생:', error);
        });
}

function createCommentElement(comment) {
    const commentElement = document.createElement('div');
    commentElement.classList.add('reply');
    if (comment.parentRno) {
        commentElement.classList.add('reply-reply');
    }

    commentElement.innerHTML = `
        <p><strong>User ID : ${comment.uid}</strong></p>
        ${comment.parentRno === null ? `
            <p>댓글 번호 : ${comment.rno}</p>
        ` : `
            <p>↳ : ${comment.parentRno}</p>
        `}
        <p>댓글 내용 : ${comment.reply}</p>
        <input type="hidden" class="rno" value="${comment.rno}">
        ${comment.uid === document.getElementById("uid").value ? `
            <button class="delete-btn" data-rno="${comment.rno}">삭제</button>
            <button class="edit-btn" data-rno="${comment.rno}" data-reply="${comment.reply}">수정</button>
        ` : ''}
        ${comment.parentRno === null ? `
            <button class="rereply-btn" data-rno="${comment.rno}">답글 달기</button>
            <button class="toggle-replies-btn" data-rno="${comment.rno}">더보기</button>
            <hr>
            <div class="reReplies"></div>
        ` : `
            <hr>
        `}
    `;
    return commentElement;
}

function attachEventListeners() {
    document.querySelectorAll('.toggle-replies-btn').forEach(button => {
        button.addEventListener('click', function() {
            const rno = this.getAttribute('data-rno');
            const parentCommentElement = document.querySelector(`.rno[value="${rno}"]`).closest('.reply');
            const repliesContainer = parentCommentElement.querySelector('.reReplies');
            repliesContainer.querySelectorAll('.reply-reply').forEach(reply => {
                reply.classList.toggle('visible');
            });
        });
    });

    document.querySelectorAll('.rereply-btn').forEach(button => {
        button.addEventListener('click', function() {
            const rno = this.getAttribute('data-rno');
            const replyValue = prompt("대댓글 내용:");
            if (replyValue) {
                const bno = document.getElementById("bno").value;
                const uid = document.getElementById("uid").value;
                postReReply(bno, uid, rno, replyValue);
            }
        });
    });

    document.querySelectorAll('.delete-btn').forEach(button => {
        button.addEventListener('click', function() {
            const rno = this.getAttribute('data-rno');
            deleteComment(rno);
        });
    });

    document.querySelectorAll('.edit-btn').forEach(button => {
        button.addEventListener('click', function() {
            const rno = this.getAttribute('data-rno');
            const currentReply = this.getAttribute('data-reply');
            editComment(rno, currentReply);
        });
    });
}





function checkLikeStatus() {
    const bno = $("#bno").val();
    const uid = $("#uid").val();

    $.ajax({
        type: "POST",
        url: "/like/status",
        data: JSON.stringify({ bno: bno, uid: uid }),
        contentType: "application/json",
        success: function(response) {
            if (response === 'liked') {
                $('#heartbtn').addClass('liked');
            }
        }
    });
}

function deleteComment(rno) {
    const uid = document.getElementById("uid").value;
    fetch(`/api/reply/${rno}?uid=${uid}`, {
        method: 'DELETE',
    })
        .then(response => {
            if (!response.ok) {
                throw new Error('삭제 실패');
            }
            alert("삭제 완료");
            getComments(document.getElementById("bno").value);
        })
        .catch(error => {
            console.error('삭제 중 오류 발생:', error);
            alert("삭제에 실패했습니다. 오류 메시지: " + error.message);
        });
}

function editComment(rno, currentReply) {
    const newReply = prompt("새 댓글 내용:", currentReply);
    if (newReply) {
        fetch(`/api/reply/${rno}`, {
            method: 'PUT',
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                uid: document.getElementById("uid").value,
                reply: newReply,
            }),
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('수정 실패');
                }
                alert("수정 완료");
                getComments(document.getElementById("bno").value);
            })
            .catch(error => {
                console.error('수정 중 오류 발생:', error);
                alert("수정에 실패했습니다. 오류 메시지: " + error.message);
            });
    }
}
