VERSION 5.00
Begin VB.Form Form1 
   BorderStyle     =   1  'Fixed Single
   Caption         =   "Launcher"
   ClientHeight    =   9150
   ClientLeft      =   1920
   ClientTop       =   1410
   ClientWidth     =   16170
   Icon            =   "Form1.frx":0000
   LinkTopic       =   "Form1"
   MaxButton       =   0   'False
   MinButton       =   0   'False
   Picture         =   "Form1.frx":038A
   ScaleHeight     =   9150
   ScaleWidth      =   16170
   Begin VB.CommandButton Command1 
      Appearance      =   0  'Flat
      Caption         =   "Launch"
      BeginProperty Font 
         Name            =   "Dungeon"
         Size            =   21.75
         Charset         =   0
         Weight          =   400
         Underline       =   0   'False
         Italic          =   0   'False
         Strikethrough   =   0   'False
      EndProperty
      Height          =   1335
      Left            =   12360
      MaskColor       =   &H8000000F&
      TabIndex        =   0
      Top             =   7440
      UseMaskColor    =   -1  'True
      Width           =   3015
   End
   Begin VB.Menu setting 
      Caption         =   "Setting"
   End
   Begin VB.Menu update 
      Caption         =   "Update"
   End
   Begin VB.Menu code 
      Caption         =   "Code"
   End
End
Attribute VB_Name = "Form1"
Attribute VB_GlobalNameSpace = False
Attribute VB_Creatable = False
Attribute VB_PredeclaredId = True
Attribute VB_Exposed = False
Private Sub code_Click()
Shell ("C:\Windows\explorer.exe https://github.com/Douyin-vbuser/Minecraft-Genshin-Mod")
End Sub

Private Sub Command1_Click()
Dim fso As Object, ts As Object
If Dir("" & App.Path & "\start.bat") = "" Then
Form2.Show
Else
Me.Hide
Form2.Hide
Shell "" & App.Path & "\start.bat"
End
End If
Set ts = Nothing
Set fso = Nothing
End Sub

Private Sub setting_Click()
Form2.Show
End Sub

Private Sub update_Click()
Form3.Show
End Sub
