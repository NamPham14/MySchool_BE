package com.fpt.myfschool.service.impl;
import com.fpt.myfschool.dto.response.ConversationDto;
import com.fpt.myfschool.dto.response.MessageDto;
import com.fpt.myfschool.mapper.ChatMapper;
import com.fpt.myfschool.repository.ConversationRepository;
import com.fpt.myfschool.repository.MessageRepository;
import com.fpt.myfschool.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ConversationRepository convRepo;
    private final MessageRepository messageRepo;
    private final ChatMapper chatMapper;

    private final com.fpt.myfschool.repository.ConversationParticipantRepository participantRepo;
    private final com.fpt.myfschool.repository.UserRepository userRepo;
    private final com.fpt.myfschool.repository.SchoolClassRepository schoolClassRepo;

    /**
     * Lấy Danh Sách Kênh Chat của User
     */
    @Override
    public List<ConversationDto> getMyConversations(Long userId) {
        com.fpt.myfschool.entity.User user = userRepo.findById(userId).orElse(null);
        if (user == null) return java.util.Collections.emptyList();

        // 1. Đồng bộ Group Chat
        // Nếu là giáo viên, cho phép vào tất cả Group Chat hiện có (hoặc tạo theo lớp)
        boolean isTeacher = user.getRoles().stream().anyMatch(r -> "TEACHER".equals(r.getName()));
        boolean isStudent = user.getRoles().stream().anyMatch(r -> "STUDENT".equals(r.getName()));

        if (isTeacher) {
            // Lấy tất cả lớp học để tạo/vào Group Chat
            List<com.fpt.myfschool.entity.SchoolClass> allClasses = schoolClassRepo.findAll();
            
            for (com.fpt.myfschool.entity.SchoolClass clazz : allClasses) {
                String groupName = "Tập thể Lớp " + clazz.getName();
                com.fpt.myfschool.entity.Conversation groupConv = convRepo.findByName(groupName)
                    .orElseGet(() -> {
                        com.fpt.myfschool.entity.Conversation newConv = com.fpt.myfschool.entity.Conversation.builder()
                            .name(groupName)
                            .type(com.fpt.myfschool.entity.Conversation.ConvType.GROUP)
                            .lastMessage("Group đã được tạo.")
                            .lastUpdated(java.time.LocalDateTime.now())
                            .build();
                        return convRepo.save(newConv);
                    });
                if (!participantRepo.existsByConversationIdAndUserId(groupConv.getId(), user.getId())) {
                    participantRepo.save(com.fpt.myfschool.entity.ConversationParticipant.builder().conversation(groupConv).user(user).build());
                }
            }

            // Tạo 1-1 với tất cả học sinh
            List<com.fpt.myfschool.entity.User> allStudents = userRepo.findByRolesName("STUDENT");
            for (com.fpt.myfschool.entity.User student : allStudents) {
                syncOneToOneChat(user, student);
            }
        } else if (isStudent) {
            // Học sinh chỉ vào Group lớp của mình
            if (user.getSchoolClass() != null) {
                String groupName = "Tập thể Lớp " + user.getSchoolClass().getName();
                com.fpt.myfschool.entity.Conversation groupConv = convRepo.findByName(groupName)
                    .orElseGet(() -> convRepo.save(com.fpt.myfschool.entity.Conversation.builder().name(groupName).type(com.fpt.myfschool.entity.Conversation.ConvType.GROUP).lastMessage("Group đã được tạo.").lastUpdated(java.time.LocalDateTime.now()).build()));
                
                if (!participantRepo.existsByConversationIdAndUserId(groupConv.getId(), user.getId())) {
                    participantRepo.save(com.fpt.myfschool.entity.ConversationParticipant.builder().conversation(groupConv).user(user).build());
                }
            }

            // Học sinh tạo 1-1 với tất cả giáo viên
            List<com.fpt.myfschool.entity.User> allTeachers = userRepo.findByRolesName("TEACHER");
            for (com.fpt.myfschool.entity.User teacher : allTeachers) {
                syncOneToOneChat(user, teacher);
            }
        }

        // 3. Trả về danh sách, sửa lại tên của 1-1 chat thành tên người kia và set unreadCount
        return participantRepo.findByUserId(userId).stream()
            .map(p -> {
                ConversationDto dto = chatMapper.toConversationDto(p.getConversation());
                if ("ONE_TO_ONE".equals(dto.getType())) {
                    List<com.fpt.myfschool.entity.ConversationParticipant> parts = participantRepo.findByConversationId(p.getConversation().getId());
                    for (com.fpt.myfschool.entity.ConversationParticipant pt : parts) {
                        if (!pt.getUser().getId().equals(userId)) {
                            dto.setName(pt.getUser().getFullName() + " (1-1)");
                            break;
                        }
                    }
                }
                Integer unreads = messageRepo.countByConversationIdAndIsReadFalseAndSenderIdNot(p.getConversation().getId(), userId);
                dto.setUnreadCount(unreads != null ? unreads : 0);
                return dto;
            })
            .collect(Collectors.toList());
    }

    private void syncOneToOneChat(com.fpt.myfschool.entity.User u1, com.fpt.myfschool.entity.User u2) {
        Long id1 = Math.min(u1.getId(), u2.getId());
        Long id2 = Math.max(u1.getId(), u2.getId());
        String chatName = "Chat_" + id1 + "_" + id2;
        
        com.fpt.myfschool.entity.Conversation p2p = convRepo.findByName(chatName)
            .orElseGet(() -> {
                com.fpt.myfschool.entity.Conversation newConv = com.fpt.myfschool.entity.Conversation.builder()
                    .name(chatName)
                    .type(com.fpt.myfschool.entity.Conversation.ConvType.ONE_TO_ONE)
                    .lastMessage("Bắt đầu trò chuyện")
                    .lastUpdated(java.time.LocalDateTime.now())
                    .build();
                return convRepo.save(newConv);
            });
            
        if (!participantRepo.existsByConversationIdAndUserId(p2p.getId(), u1.getId())) {
            participantRepo.save(com.fpt.myfschool.entity.ConversationParticipant.builder().conversation(p2p).user(u1).build());
        }
        if (!participantRepo.existsByConversationIdAndUserId(p2p.getId(), u2.getId())) {
            participantRepo.save(com.fpt.myfschool.entity.ConversationParticipant.builder().conversation(p2p).user(u2).build());
        }
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public List<MessageDto> getMessagesByConversation(Long conversationId) {
        // Lấy User hiện tại
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof com.fpt.myfschool.security.UserDetailsImpl) {
            Long currentUserId = ((com.fpt.myfschool.security.UserDetailsImpl) auth.getPrincipal()).getId();
            // Đánh dấu đã đọc các tin nhắn của người khác
            List<com.fpt.myfschool.entity.Message> unreadMessages = messageRepo.findByConversationIdOrderBySentAtAsc(conversationId).stream()
                .filter(m -> Boolean.FALSE.equals(m.getIsRead()) && !m.getSender().getId().equals(currentUserId))
                .collect(Collectors.toList());
            if (!unreadMessages.isEmpty()) {
                unreadMessages.forEach(m -> m.setIsRead(true));
                messageRepo.saveAll(unreadMessages);
            }
        }

        return messageRepo.findByConversationIdOrderBySentAtAsc(conversationId).stream()
                .map(chatMapper::toMessageDto).collect(Collectors.toList());
    }

    @Override
    public MessageDto saveMessage(Long conversationId, Long senderId, String content) {
        com.fpt.myfschool.entity.Conversation conv = convRepo.findById(conversationId)
                .orElseThrow(() -> new com.fpt.myfschool.exception.AppException(com.fpt.myfschool.exception.ErrorCode.RESOURCE_NOT_FOUND));
        
        com.fpt.myfschool.entity.User sender = userRepo.findById(senderId)
                .orElseThrow(() -> new com.fpt.myfschool.exception.AppException(com.fpt.myfschool.exception.ErrorCode.USER_NOT_FOUND));

        com.fpt.myfschool.entity.Message msg = com.fpt.myfschool.entity.Message.builder()
                .conversation(conv)
                .sender(sender)
                .content(content)
                .isRead(false)
                .sentAt(java.time.LocalDateTime.now())
                .build();
        
        messageRepo.save(msg);
        return chatMapper.toMessageDto(msg);
    }

    @Override
    @org.springframework.transaction.annotation.Transactional
    public void resetAllChats() {
        messageRepo.deleteAll();
        participantRepo.deleteAll();
        convRepo.deleteAll();
    }
}