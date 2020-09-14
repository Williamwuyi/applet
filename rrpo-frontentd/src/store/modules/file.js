export default {
  namespaced: true,
  state: {
    editorList: [],
    appendixList: []
  },
  mutations: {
    addEditorFile (state, file) {
      state.editorList.push(file)
    },
    deleteEditorFile (state, file) {
      state.editorList = state.editorList.filter(item => item.fileId !== file.fileId)
    },
    initEditorList (state) {
      state.editorList = []
    },
    addAppendixFile (state, file) {
      state.appendixList.push(file)
    },
    deleteAppendixFile (state, file) {
      state.appendixList = state.appendixList.filter(item => item.fileId !== file.fileId)
    },
    initAppendixList (state) {
      state.appendixList = []
    }
  }
}
