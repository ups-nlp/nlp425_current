'''user input, model
model call 
output response 
output to java or to a text file'''


#we will need to perform this reverse-lookup for every word in a predicted sequence
#this method returns the prediction in words (not integers)
def get_prediction(model, tokenizer, source):
    prediction = model.predict(source, verbose=1)
    integers = [argmax(vector) for vector in prediction]
    target = list()
    for i in integers:
        word = get_word(i, tokenizer)
        if word is None:
            break
        target.append(word)
    return " ".join(target)
